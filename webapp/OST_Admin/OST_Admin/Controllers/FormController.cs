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

namespace OST_Admin.Controllers
{
    public class FormController : Controller
    {
        private OSTDataContext db = new OSTDataContext();

        //
        // GET: /Form/

        public ActionResult Index()
        {
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

        public PartialViewResult _PartialQuestionList(int formId, string action1, string type)
        {
            Form form;
            form = db.Forms.Where(p => p.FormId == formId).Single();

            if (action1 == "Add")
            {
                if (type == "Text")
                {
                    TextQuestion tq = new TextQuestion();

                    //this will put the item at the bottom of the list.
                    tq.SortOrder = form.Questions.Count;

                    form.Questions.Add(tq);
                    db.SaveChanges();
                }
                else if (type == "Choice")
                {
                    ChoiceQuestion cq = new ChoiceQuestion();

                    //this will put the item at the bottom of the list.
                    cq.SortOrder = form.Questions.Count;

                    Option o = new Option();

                    //this will put the item at the bottom of the list.
                    o.SortOrder = 0;
                    cq.Options.Add(o);

                    form.Questions.Add(cq);
                    db.SaveChanges();
                }
                else if (type == "Likert")
                {
                    LikertScaleQuestion lsq = new LikertScaleQuestion();

                    //this will put the item at the bottom of the list.
                    lsq.SortOrder = form.Questions.Count;

                    form.Questions.Add(lsq);
                    db.SaveChanges();
                }
            }

            return PartialView(form.Questions);
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

        public ActionResult Edit(int id)
        {
            Form form;
            form = db.Forms.Where(p => p.FormId == id).Single();
            

            ViewBag.formId = id;
           
            return View(form);
        }

        [HttpPost]
        public ActionResult Edit(Form form, List<Question> qlist)
        {
            ViewBag.formId = form.FormId;

            if (ModelState.IsValid)
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

                if (qlist !=null)
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

                        old_form.Questions.Attach(cq);

                        //((ChoiceQuestion)question).Other = ((ChoiceQuestion)q).Other;
                    }
                    else if (question is LikertScaleQuestion)
                    {
                        //TODO
                    }


                }

                

                //db.Forms.Attach(form);
                db.SaveChanges();

                return RedirectToAction("Index");
            }

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
    }
}