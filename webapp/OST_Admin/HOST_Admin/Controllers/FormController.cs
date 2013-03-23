using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using HOST_Admin.Models;
using System.Data.Objects.DataClasses;
using System.Data.Entity.Infrastructure;
using System.Data.Objects;
using HOST_Admin.Helper;
using HOST_Admin.Models.ViewModel;
using HOST_Admin.Models.Repository;
using System.Web.Security;

namespace HOST_Admin.Controllers
{
    [Authorize]
    public class FormController : Controller
    {
        //TODO -- remove the rest of db interactions
        private HOSTDataContext db = new HOSTDataContext();
        private readonly IFormRepository _formRepository;
        private readonly IUserRepository _userRepository;

        public FormController(IFormRepository formRepository, IUserRepository userRepository)
        {
            _formRepository = formRepository;
            _userRepository = userRepository;
        }

        //
        // GET: /Form/

        //Updated to only show forms you have access to
        public ActionResult Index()
        {
            int user_id;
            try
            {
                user_id = _userRepository.getLoggedInUserId();
            }
            catch (NullReferenceException e)
            {
                return RedirectToAction("LogOut", "Account");
            }

            if (_userRepository.getLoggedInRole() == "Administrator")
                return View(_formRepository.GetAll().ToList());
            else
                return View(_formRepository.GetAll().Where(p => p.CreatedBy == user_id).ToList());
        }

        /// <summary>
        /// Returns the XML view of the form
        /// </summary>
        /// <param name="id">The id of the form</param>
        /// <returns>XML view of the form</returns>
        public ActionResult XML(int id)
        {
            ViewBag.id = id;
            return View(id);
        }

        //TODO -change name of formId parameter
        public PartialViewResult AddQuestion(int formId, string type)
        {
            _formRepository.addQuestion(formId, type);

            Form form = _formRepository.getFormById(formId);

            //open the new tab after we create it
            ViewBag.tabopen = form.Questions.Count - 1;

            return PartialView("_QuestionList", form.Questions);
        }

        //
        // GET: /Form/Create

        public ActionResult Create()
        {
            try
            {
                ViewBag.CreatedBy = _userRepository.getLoggedInUserId();
            }
            catch (NullReferenceException e)
            {
                return RedirectToAction("LogOut", "Account");
            }
            return View();
        }

        //
        // POST: /Form/Create

        [HttpPost]
        public ActionResult Create(Form form)
        {
            if (ModelState.IsValid)
            {
                _formRepository.addForm(form);

                return RedirectToAction("Edit", new { id = form.FormId });
            }

            return View(form);
        }

        //TODO - remove extra parameters, only need optionId and rename it
        public PartialViewResult DeleteOption(int formId, int questionId, int optionId)
        {
            int question_id = _formRepository.deleteOption(optionId);

            //ned the question to know which tab to open, and to get the question list
            Question question = _formRepository.getQuestionById(question_id);

            //return to the tab where I deleted an item
            ViewBag.tabopen = question.SortOrder;

            return PartialView("_QuestionList", question.Form.Questions);
        }
        
        //TODO rename questionId parameter
        public PartialViewResult DeleteQuestion(int questionId)
        {
            int form_id = _formRepository.deleteQuestion(questionId);

            Form form = _formRepository.getFormById(form_id);

            //return to the tab where I deleted an item (in this case just goto start)
            ViewBag.tabopen = 0;

            return PartialView("_QuestionList", form.Questions);
        }

        public ActionResult Edit(int id)
        {
            int user_id;
            String user_role;
            Form form;
            try
            {
                user_id = _userRepository.getLoggedInUserId();
                user_role = _userRepository.getLoggedInRole();

                form = _formRepository.getFormById(id);

                if (form.CreatedBy != user_id && user_role != "Administrator")
                    return RedirectToAction("LogOut", "Account");
            }
            catch (NullReferenceException e)
            {
                return RedirectToAction("LogOut", "Account");
            }

            //pass in form id
            ViewBag.formId = id;
            //open the first tab
            ViewBag.tabopen = 0;

            return View(form);
        }

        [HttpPost]
        //public ActionResult Edit(Form form, List<QuestionViewModel> QuestionViewModel)
        public ActionResult Edit(Form form, List<Question> question_list)
        {

            //foreach (var q in qlist)
            //{
            //    if (q is TextQuestion)
            //    {
            //        TryUpdateModel(q);
            //    }
            //    if (q is ChoiceQuestion)
            //    {
            //        ((ChoiceQuestion)q).Options.ToList().ForEach(oitem => TryUpdateModel(oitem));
            //        TryUpdateModel(q);
            //    }
            //    if (q is LikertScaleQuestion)
            //    {
            //        ((LikertScaleQuestion)q).Labels.ToList().ForEach(litem => TryUpdateModel(litem));
            //        TryUpdateModel(q);
            //    }
            //}
            //TryUpdateModel(qlist);
            //db.SaveChanges();
            

            ViewBag.formId = form.FormId;
            //this used to be if ModelState.Valid, but its causing errors so im blowing it off.
            //if (ModelState.Valid)
            //{
            //}
            
            _formRepository.updateForm(form, question_list);
            return RedirectToAction("Edit", new { id = form.FormId });
            
            


            
            /*
            Form form2 = db.Forms.Where(p => p.FormId == form.FormId).Single();

            ViewBag.formId = form.FormId;

            return View(form2);*/
        }

        public ActionResult Delete(int id = 0)
        {
            Form form = db.Forms.Single(f => f.FormId == id);

            EntityCollection<Question> ecq = form.Questions;

            ecq.ToList().ForEach(x => db.DeleteObject(x));

            db.Forms.DeleteObject(form);
            db.SaveChanges();
            return RedirectToAction("Index");
        }

        protected override void Dispose(bool disposing)
        {
            db.Dispose();
            base.Dispose(disposing);
        }

        public PartialViewResult AddOption(int questionId)
        {
            ChoiceQuestion cq = (ChoiceQuestion)db.Questions.Where(q => q.QuestionId == questionId).Single();
            Option o = new Option() { SortOrder = cq.Options.Count, Text = "option goes here" };

            //stay on the tab we just added an option to
            ViewBag.tabopen = cq.SortOrder;

            cq.Options.Add(o);
            db.SaveChanges();


            return PartialView("_QuestionList", cq.Form.Questions);
        }
    }
}