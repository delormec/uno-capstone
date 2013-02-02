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

namespace OST_Admin.Controllers
{
    public class FormController : Controller
    {
        private OSTDataContext db = new OSTDataContext();

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


        public PartialViewResult AddQuestion(int formId, string type)
        {
            Form form;
            form = db.Forms.Where(p => p.FormId == formId).Single();

            if (type == "Text")
            {
                TextQuestion tq = new TextQuestion();

                //this will put the item at the bottom of the list.
                tq.SortOrder = form.Questions.Count;

                //open the new tab after we create it
                ViewBag.tabopen = form.Questions.Count;

                form.Questions.Add(tq);
                db.SaveChanges();
            }
            else if (type == "Choice")
            {
                ChoiceQuestion cq = new ChoiceQuestion();

                //this will put the item at the bottom of the list.
                cq.SortOrder = form.Questions.Count;

                //open the new tab after we create it
                ViewBag.tabopen = form.Questions.Count;

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

                //open the new tab after we create it
                ViewBag.tabopen = form.Questions.Count;

                List<Label> ll = new List<Label>() { new Label() { Range = "low" }, new Label() { Range = "medium" }, new Label() { Range = "high" } };
                    
                //add them all to the likert scale question
                ll.ForEach(l => lsq.Labels.Add(l));

                form.Questions.Add(lsq);
                db.SaveChanges();
            }
      

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

        public PartialViewResult DeleteOption(int formId, int questionId, int optionId)
        {
            Form form;// = db.Forms.Single(f => f.FormId == formId);

            //ChoiceQuestion q = form.Questions.Where(q => q.QuestionId == questionId);

            Option option = db.Options.Where(o => o.OptionId == optionId).Single();

            //return to the tab where I deleted an item
            ViewBag.tabopen = option.ChoiceQuestion.SortOrder;

            // don't let them delete the last option
            if (option.ChoiceQuestion.Options.Count == 1)
            {
                return PartialView("_QuestionList", option.ChoiceQuestion.Form.Questions);
            }

            db.DeleteObject(option);
            db.SaveChanges();

            ChoiceQuestion cq = (ChoiceQuestion)db.Questions.Where(q => q.QuestionId == questionId).Single();

            int neworder = 0;
            cq.Options.ToList().ForEach(x => x.SortOrder = neworder++);

            db.SaveChanges();

            //foreach (cq.Options.ToList() 

            //reorder the list now that we've removed one
            form = db.Forms.Single(f => f.FormId == formId);


            //Option = q.Options.Where(o => o.OptionId)


            return PartialView("_QuestionList", form.Questions);
        }

        public PartialViewResult DeleteQuestion(int questionId)
        {
            Form form;// = db.Forms.Single(f => f.FormId == formId);
            Question question = db.Questions.Where(q => q.QuestionId == questionId).Single();
            form = question.Form;

            // don't let them delete the last question (actually let them)
            //if (question.Form.Questions.Count == 1)
            //{
            //    return PartialView("_QuestionList", question.Form.Questions);
            //}

            db.DeleteObject(question);
            db.SaveChanges();

            //reorder the remaining questions
            int neworder = 0;
            form.Questions.ToList().ForEach(x => x.SortOrder = neworder++);

            db.SaveChanges();

            //return to the tab where I deleted an item (in this case just goto start)
            ViewBag.tabopen = 0;


            return PartialView("_QuestionList", form.Questions);
        }

        public ActionResult Edit(int id)
        {
            Form form;
            form = db.Forms.Where(p => p.FormId == id).Single();
            

            ViewBag.formId = id;

            //open the first tab
            ViewBag.tabopen = 0;
           
            return View(form);
        }

        [HttpPost]
        //public ActionResult Edit(Form form, List<QuestionViewModel> QuestionViewModel)
        public ActionResult Edit(Form form, List<Question> qlist)
        {

            qlist.ToList().ForEach(qitem => TryUpdateModel(qitem));
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