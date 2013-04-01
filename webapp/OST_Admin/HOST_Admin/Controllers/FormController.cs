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
    /// <summary>
    /// Handles the management of forms. [Authorize] tag requires that only authorized users are allowed.
    /// </summary>
    [Authorize]
    public class FormController : Controller
    {
        /// <summary>
        /// Reference to form repository.
        /// </summary>
        private readonly IFormRepository _formRepository;

        /// <summary>
        /// Reference to user repository.
        /// </summary>
        private readonly IUserRepository _userRepository;

        /// <summary>
        /// Base constructor, MVC calls this constructor behind the scenes.
        /// </summary>
        /// <param name="formRepository">It is possible to pass something that implements this interface for testing purposes.</param>
        /// <param name="userRepository">It is possible to pass something that implements this interface for testing purposes.</param>
        public FormController(IFormRepository formRepository, IUserRepository userRepository)
        {
            _formRepository = formRepository;
            _userRepository = userRepository;
        }

        /// <summary>
        /// GET: Form/Index: If you are an admin it displays a list of all the forms in a system. A normal user only sees forms they've created.
        /// </summary>
        /// <returns>Admin: All forms, User: Only their forms; if session expired return to log in screen.</returns>
        public ActionResult Index()
        {
            int user_id;

            //If the session variable has expired then logout
            try
            {
                user_id = _userRepository.getLoggedInUserId();
            }
            catch (NullReferenceException e)
            {
                return RedirectToAction("LogOut", "Account");
            }

            //If admin display all, otherwise only display your own
            if (_userRepository.getLoggedInRole() == "Administrator")
                return View(_formRepository.GetAll().ToList());
            else
                return View(_formRepository.GetAll().Where(p => p.CreatedBy == user_id).ToList());
        }

        /// <summary>
        /// GET: Form/XML: Returns the XML view of the form
        /// </summary>
        /// <param name="id">The id of the form</param>
        /// <returns>XML view of the form</returns>
        public ActionResult XML(int id)
        {
            ViewBag.id = id;
            return View(id);
        }

        /// <summary>
        /// AJAX: Form/AddQuestion: Add a question of 'type' to a particular form.
        /// </summary>
        /// <param name="form_id">id of form you want to add question to</param>
        /// <param name="type">type of question you want to add ("Text", "Likert", "Choice")</param>
        /// <returns>Returns the whole question tab to the caller.</returns>
        public PartialViewResult AddQuestion(int form_id, string type)
        {
            _formRepository.addQuestion(form_id, type);

            Form form = _formRepository.getFormById(form_id);

            //open the new tab after we create it
            ViewBag.tabopen = form.Questions.Count - 1;

            return PartialView("_QuestionList", form.Questions);
        }

        /// <summary>
        /// Deep copy a form.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public ActionResult Copy(int id)
        {
            try
            {
                Form form = _formRepository.getFormById(id);
                _formRepository.copyForm(form, _userRepository.getLoggedInUserId());
            }
            catch (NullReferenceException e)
            {
                return RedirectToAction("LogOut", "Account");
            }

            return RedirectToAction("Index");
        }

        /// <summary>
        /// GET: Form/Create: Allows creation of new forms.
        /// </summary>
        /// <returns></returns>
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

            List<SelectListItem> fieldtypes = new List<SelectListItem>(){
                                        new SelectListItem() {Text = "Choice / Single-line text", Value="SINGLE"},
                                        new SelectListItem() {Text = "Multi-line text", Value="MULTI"},
                                        new SelectListItem() {Text = "Date", Value="DATE"},
                                        new SelectListItem() {Text = "Currency / Number", Value="NUMBER"}
                                    };

            ViewBag.FieldTypes = fieldtypes;

            return View();
        }

        /// <summary>
        /// POST: Form/Create: takes a form from the create screen and saves it to the database.
        /// </summary>
        /// <param name="form">Form from create screen.</param>
        /// <returns>Redirects to edit screen if form was valid.</returns>
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

        /// <summary>
        /// AJAX: Form/DeleteOption: delete the specified option from the choice question.
        /// </summary>
        /// <param name="option_id">Option id you want to delete.</param>
        /// <returns>Returns the whole question tab to the caller.</returns>
        public PartialViewResult DeleteOption(int option_id)
        {
            int question_id = _formRepository.deleteOption(option_id);

            //ned the question to know which tab to open, and to get the question list
            Question question = _formRepository.getQuestionById(question_id);

            //return to the tab where I deleted an item
            ViewBag.tabopen = question.SortOrder;

            return PartialView("_QuestionList", question.Form.Questions);
        }
        
        /// <summary>
        /// AJAX: Form/DeleteQuestion: delete the specified question from form.
        /// </summary>
        /// <param name="question_id">Question id you want to delete.</param>
        /// <returns>Returns the whole question tab to the caller.</returns>
        public PartialViewResult DeleteQuestion(int question_id)
        {
            int form_id = _formRepository.deleteQuestion(question_id);

            Form form = _formRepository.getFormById(form_id);

            //return to the tab where I deleted an item (in this case just goto start)
            ViewBag.tabopen = 0;

            return PartialView("_QuestionList", form.Questions);
        }

        /// <summary>
        /// GET: Form/Edit: Allows user to edit a specified form.
        /// </summary>
        /// <param name="id">Form id you want to edit</param>
        /// <returns>If admin or a user's form then display edit page, logs out if session variable expired.</returns>
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

            //set up dropdown list for field_types
            List<SelectListItem> fieldtypes = new List<SelectListItem>(){
                                        new SelectListItem() {Text = "Choice / Single-line text", Value="SINGLE"},
                                        new SelectListItem() {Text = "Multi-line text", Value="MULTI"},
                                        new SelectListItem() {Text = "Date", Value="DATE"},
                                        new SelectListItem() {Text = "Currency / Number", Value="NUMBER"}
                                    };

            ViewBag.FieldTypes = fieldtypes;

            return View(form);
        }

        /// <summary>
        /// POST: Form/Edit: Save form to database in two parts, form and question_list.
        /// </summary>
        /// <param name="form">Form meta data to be saved.</param>
        /// <param name="question_list">List of questions to be saved.</param>
        /// <returns>Returns to edit page when saved.</returns>
        [HttpPost]
        public ActionResult Edit(Form form, List<Question> question_list)
        {
            ViewBag.formId = form.FormId;

            _formRepository.updateForm(form, question_list);
            return RedirectToAction("Edit", new { id = form.FormId });
        }

        /// <summary>
        /// POST: Form/Delete: Delete a form from database.
        /// </summary>
        /// <param name="id">Form id you wish to delete.</param>
        /// <returns>Returns to form/index.</returns>
        public ActionResult Delete(int id)
        {
            _formRepository.deleteFormById(id);

            return RedirectToAction("Index");
        }

        /// <summary>
        /// AJAX: Form/AddOption: Adds an option to the question you specify.
        /// </summary>
        /// <param name="questionId">Question id you want to add an option too.</param>
        /// <returns>Returns the whole question tab to the caller.</returns>
        public PartialViewResult AddOption(int question_id)
        {
            ChoiceQuestion cq;
            _formRepository.addOption(question_id);

            cq = (ChoiceQuestion)_formRepository.getQuestionById(question_id);

            //stay on the tab we just added an option to
            ViewBag.tabopen = cq.SortOrder;

            return PartialView("_QuestionList", cq.Form.Questions);
        }

        /// <summary>
        /// AJAX: Form/ChangeQuestionPosition: Move a question to a new position
        /// </summary>
        /// <param name="question_id"></param>
        /// <param name="start_position"></param>
        /// <param name="end_position"></param>
        /// <returns>Returns the whole question tab to the caller.</returns>
        public PartialViewResult ChangeQuestionPosition(int question_id, int start_position, int end_position)
        {
            _formRepository.changeQuestionPosition(question_id, start_position, end_position);

            Question q = _formRepository.getQuestionById(question_id);

            //goto the tab of the question we moved
            ViewBag.tabopen = q.SortOrder;

            return PartialView("_QuestionList", q.Form.Questions);
        }
    }
}