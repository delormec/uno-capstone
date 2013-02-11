using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Microsoft.Data.OData;
using System.Web.Http;

namespace OST_Admin.Models.Repository
{
    public class FormRepository : IFormRepository
    {
        OSTDataContext _databaseContext;

        public FormRepository(IOSTDataContext databaseContext)
        {
            _databaseContext = new OSTDataContext();
        }

       
        public System.Linq.IQueryable<Form> GetAll()
        {
            return _databaseContext.Forms;
        }

        public Form getFormById(int id)
        {
            return _databaseContext.Forms.Where(p => p.FormId == id).Single();
        }

        public void deleteFormById(int id)
        {
            Form form;

            form = getFormById(id);
            form.Deleted = true;

            _databaseContext.SaveChanges();
        }


        public void addQuestion(int form_id, string question_type)
        {
            Form form = null;
            Question question = null;

            form = getFormById(form_id);

            //never gets here, oh well
            if (form == null)
            {
                throw new System.ArgumentException("Invalid Form Id");
            }

            if (question_type == "Text")
            {
                TextQuestion tq = new TextQuestion();

                //this will put the item at the bottom of the list.
                tq.SortOrder = form.Questions.Count;
                question = tq;

                form.Questions.Add(tq);
            }
            else if (question_type == "Choice")
            {
                ChoiceQuestion cq = new ChoiceQuestion();

                //this will put the item at the bottom of the list.
                cq.SortOrder = form.Questions.Count;

                Option o = new Option();

                //this will put the item at the bottom of the list.
                o.SortOrder = 0;
                cq.Options.Add(o);

                question = cq;
                form.Questions.Add(cq);
            }
            else if (question_type == "Likert")
            {
                LikertScaleQuestion lsq = new LikertScaleQuestion();

                //this will put the item at the bottom of the list.
                lsq.SortOrder = form.Questions.Count;

                List<Label> ll = new List<Label>() { new Label() { Range = "low" }, new Label() { Range = "medium" }, new Label() { Range = "high" } };

                //add them all to the likert scale question
                ll.ForEach(l => lsq.Labels.Add(l));

                question = lsq;
            }
            else
            {
                throw new System.ArgumentException("Invalid Question Type");
            }

            question.Text = "Question Title";
            question.HelpText = "Help text";

            form.Questions.Add(question);
            _databaseContext.SaveChanges();
        }

        public int deleteQuestion(int question_id)
        {
            Form form;// = db.Forms.Single(f => f.FormId == formId);
            Question question = _databaseContext.Questions.Where(q => q.QuestionId == question_id).Single();
            form = question.Form;

            // don't let them delete the last question (actually let them)
            //if (question.Form.Questions.Count == 1)
            //{
            //    return PartialView("_QuestionList", question.Form.Questions);
            //}

            _databaseContext.DeleteObject(question);
            _databaseContext.SaveChanges();

            //reorder the remaining questions
            int neworder = 0;
            form.Questions.ToList().ForEach(x => x.SortOrder = neworder++);

            _databaseContext.SaveChanges();

            return form.FormId;
        }


        public void addOption(int question_id)
        {
            throw new NotImplementedException();
        }

        public int deleteOption(int option_id)
        {
            Option option = _databaseContext.Options.Where(o => o.OptionId == option_id).Single();
            int question_id = option.ChoiceQuestion.QuestionId;

            // don't let them delete the last option
            if (option.ChoiceQuestion.Options.Count == 1)
            {
                return question_id;
            }

            _databaseContext.DeleteObject(option);
            
            //reorder the sort order of all the questions, starting at 0
            int neworder = 0;
            ChoiceQuestion cq = (ChoiceQuestion)_databaseContext.Questions.Where(q => q.QuestionId == question_id).Single();
            cq.Options.ToList().ForEach(x => x.SortOrder = neworder++);

            _databaseContext.SaveChanges();

            return question_id;
        }


        public Question getQuestionById(int question_id)
        {
            return _databaseContext.Questions.Where(q => q.QuestionId == question_id).Single();
        }


        public void addForm(Form form)
        {
            form.DateCreated = DateTime.Now;
            _databaseContext.Forms.AddObject(form);
            _databaseContext.SaveChanges();
        }


        public void updateForm(Form form)
        {
            Form old_form;

            old_form = _databaseContext.Forms.Where(p => p.FormId == form.FormId).Single();

            old_form.AutoUpdate = form.AutoUpdate;
            old_form.Content = form.Content;
            old_form.DateCreated = form.DateCreated;
            old_form.Deleted = form.Deleted;
            old_form.Description = form.Description;
            old_form.Name = form.Name;

            _databaseContext.SaveChanges();
        }


        //TODO -- CLEAN THIS UP
        public void updateForm(Form form, List<Question> question_list)
        {
            Form old_form = _databaseContext.Forms.Where(p => p.FormId == form.FormId).Single();

            old_form.AutoUpdate = form.AutoUpdate;
            old_form.Deleted = form.Deleted;
            old_form.Description = form.Description;
            old_form.Name = form.Name;
            old_form.URL = form.URL;
            old_form.Deleted = form.Deleted;
            old_form.Content = form.Content;
            old_form.DateCreated = form.DateCreated;

            //List<Question> qlist = new List<Question>();

            if (question_list != null && question_list.Count() > 0)
                foreach (var q in question_list)
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
            _databaseContext.SaveChanges();
        }




    }
}