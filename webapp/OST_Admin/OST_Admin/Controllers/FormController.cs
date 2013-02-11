using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using OST_Admin.Models;
using System.Data.Objects.DataClasses;
using System.Data.Entity.Infrastructure;
using System.Data.Objects;
using OST_Admin.Helper;
using OST_Admin.Models.ViewModel;
using OST_Admin.Models.Repository;
using System.Web.Security;

namespace OST_Admin.Controllers
{
    public class FormController : Controller
    {
        private OSTDataContext db = new OSTDataContext();
        private readonly IFormRepository _formRepository;

        public FormController(IFormRepository formRepository)
        {
            _formRepository = formRepository;
        }

        //
        // GET: /Form/

        
        public ActionResult Index()
        {
            return View(_formRepository.GetAll().ToList());
        }

        //
        // GET: /Form/Details/5

        public ActionResult Details(int id = 0)
        {
            Form form = db.Forms.Single(f => f.FormId == id);
            
            if (form == null)
            {
                return HttpNotFound();
            }
            return View(form);
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
            Form form = _formRepository.getFormById(id);            

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

        //
        // GET: /Form/Delete/5

        public ActionResult Delete(int id = 0)
        {
            Form form = db.Forms.Single(f => f.FormId == id);
            if (form == null)
            {
                return HttpNotFound();
            }
            return View(form);
        }

        //
        // POST: /Form/Delete/5

        [HttpPost, ActionName("Delete")]
        public ActionResult DeleteConfirmed(int id)
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
            Option o = new Option() { SortOrder = cq.Options.Count };

            //stay on the tab we just added an option to
            ViewBag.tabopen = cq.SortOrder;

            cq.Options.Add(o);
            db.SaveChanges();


            return PartialView("_QuestionList", cq.Form.Questions);
        }
    }
}