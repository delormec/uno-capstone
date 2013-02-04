﻿using System;
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
            System.Console.Write("test");
            return View(db.Forms.ToList());
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
                form.DateCreated = DateTime.Now;
                db.Forms.AddObject(form);
                db.SaveChanges();

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
        public ActionResult Edit(Form form, List<Question> qlist)
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
            if (true)
            {
                Form old_form = db.Forms.Where(p => p.FormId == form.FormId).Single();

                old_form.AutoUpdate = form.AutoUpdate;
                old_form.Deleted = form.Deleted;
                old_form.Description = form.Description;
                old_form.Name = form.Name;
                old_form.URL = form.URL;
                old_form.Deleted = form.Deleted;
                old_form.Content = form.Content;
                old_form.DateCreated = form.DateCreated;

                //List<Question> qlist = new List<Question>();

                if (qlist !=null && qlist.Count() > 0)
                foreach (var q in qlist)
                {
                    Question question = old_form.Questions.Where(p => p.QuestionId == q.QuestionId).Single();

                    if (question is TextQuestion)
                    {
                        question.Text = q.Text;
                        question.HelpText = q.HelpText;
                        question.SortOrder = q.SortOrder;
                        question.FieldName = q.FieldName;
                        question.FieldType = q.FieldType;

                        old_form.Questions.Attach(question);
                        //do nothing yet
                    }
                    else if (question is ChoiceQuestion)
                    {
                        ChoiceQuestion cq = (ChoiceQuestion)old_form.Questions.Where(p => p.QuestionId == q.QuestionId).Single();
                        ChoiceQuestion q2 = (ChoiceQuestion)q;

                        cq.Other = q2.Other;

                        cq.Text = q2.Text;
                        cq.HelpText = q2.HelpText;
                        cq.SortOrder = q2.SortOrder;
                        cq.FieldName = q2.FieldName;
                        cq.FieldType = q2.FieldType;
                        //cq.Options.ToList().ForEach(qoption => db.DeleteObject(qoption));
                        //cq.Options.ToList().ForEach(qoption => db.Detach(qoption));
                        //cq.Options.ToList().ForEach(qoption => ));

                        q2.Options.ToList().ForEach(qoption => cq.Options.Where(cqoption => cqoption.OptionId == qoption.OptionId).Single().Text = qoption.Text);
                        //q2.Options.ToList().ForEach(qoption => cq.Options.Attach(qoption));
                        //cq.Options.Attach = q2.Options;
                        old_form.Questions.Attach(cq);

                        //((ChoiceQuestion)question).Other = ((ChoiceQuestion)q).Other;
                    }
                    else if (question is LikertScaleQuestion)
                    {
                        LikertScaleQuestion lq = (LikertScaleQuestion)old_form.Questions.Where(p => p.QuestionId == q.QuestionId).Single();
                        LikertScaleQuestion q2 = (LikertScaleQuestion)q;

                        lq.Steps = q2.Steps;

                        lq.Text = q2.Text;
                        lq.HelpText = q2.HelpText;
                        lq.SortOrder = q2.SortOrder;
                        lq.FieldName = q2.FieldName;
                        lq.FieldType = q2.FieldType;

                        lq.Labels.ToList().Count();

                        q2.Labels.ToList().ForEach(qlabel => lq.Labels.Where(cqlabel => cqlabel.LabelId == qlabel.LabelId).Single().Text = qlabel.Text);

                        old_form.Questions.Attach(lq);
                    }


                }

                

                //db.Forms.Attach(form);
                db.SaveChanges();

                return RedirectToAction("Index");
            }
            

            
            /*
            Form form2 = db.Forms.Where(p => p.FormId == form.FormId).Single();

            ViewBag.formId = form.FormId;

            return View(form2);*/
            return View(form);
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