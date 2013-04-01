using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Microsoft.Data.OData;
using System.Web.Http;
using System.Data.Objects.DataClasses;

namespace HOST_Admin.Models.Repository
{
    /// <summary>
    /// Model layer repository that handles all interactions with Form objects
    /// </summary>
    public class FormRepository : IFormRepository
    {
        /// <summary>
        /// Interface with EntityFramework (database)
        /// </summary>
        HOSTDataContext _databaseContext;

        /// <summary>
        /// Base constructor, MVC calls this constructor behind the scenes.
        /// </summary>
        /// <param name="databaseContext">It is possible to pass something that implements this interface for testing purposes.</param>
        public FormRepository(IHOSTDataContext databaseContext)
        {
            _databaseContext = new HOSTDataContext();
        }

        /// <summary>
        /// Get all forms.
        /// </summary>
        /// <returns></returns>
        public IQueryable<Form> GetAll()
        {
            return _databaseContext.Forms;
        }

        /// <summary>
        /// Get form by form id.
        /// </summary>
        /// <param name="form_id"></param>
        /// <returns></returns>
        public Form getFormById(int form_id)
        {
            return _databaseContext.Forms.Where(p => p.FormId == form_id).Single();
        }

        /// <summary>
        /// Delete form by form id.
        /// </summary>
        /// <param name="form_id"></param>
        public void deleteFormById(int form_id)
        {
            Form form = _databaseContext.Forms.Single(f => f.FormId == form_id);

            EntityCollection<Question> ecq = form.Questions;

            //each question in a form must be deleted first
            ecq.ToList().ForEach(x => _databaseContext.DeleteObject(x));

            //finally delete the form itself
            _databaseContext.Forms.DeleteObject(form);

            _databaseContext.SaveChanges();
        }

        /// <summary>
        /// Add a question to a given form. Throws argument exception if unkown type is passed in.
        /// </summary>
        /// <param name="form_id"></param>
        /// <param name="question_type">One of the following: 'Choice' 'Text' 'Likert'</param>
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
                tq.FieldType = "MULTI";
                question = tq;

                form.Questions.Add(tq);
            }
            else if (question_type == "Choice")
            {
                ChoiceQuestion cq = new ChoiceQuestion();

                //this will put the item at the bottom of the list.
                cq.SortOrder = form.Questions.Count;
                cq.FieldType = "SINGLE";

                Option o = new Option();

                //this will put the item at the bottom of the list.
                o.SortOrder = 0;
                o.Text = "option goes here";
                cq.Options.Add(o);

                question = cq;
                form.Questions.Add(cq);
            }
            else if (question_type == "Likert")
            {
                LikertScaleQuestion lsq = new LikertScaleQuestion();

                //this will put the item at the bottom of the list.
                lsq.SortOrder = form.Questions.Count;
                lsq.FieldType = "SINGLE";

                List<Label> ll = new List<Label>() { new Label() { Range = "low", Text = "Strongly Disagree" }, new Label() { Range = "high", Text = "Strongly Agree" } };

                //add them all to the likert scale question
                ll.ForEach(l => lsq.Labels.Add(l));
                lsq.Steps = 10;

                question = lsq;
            }
            else
            {
                throw new System.ArgumentException("Invalid Question Type");
            }

            question.Text = "enter question here";
            question.HelpText = "enter help text here";
            question.FieldName = "FIELDNAME";
            //question.FieldType = "STRING";

            form.Questions.Add(question);
            _databaseContext.SaveChanges();
        }

        /// <summary>
        /// Delete a question from a form.
        /// </summary>
        /// <param name="question_id"></param>
        /// <returns>(int) id of the form from which the question was removed.</returns>
        public int deleteQuestion(int question_id)
        {
            Form form;
            //Find the question that is to be removed, and the form of which it is apart of
            Question question = _databaseContext.Questions.Where(q => q.QuestionId == question_id).Single();
            form = question.Form;

            _databaseContext.DeleteObject(question);
            _databaseContext.SaveChanges();

            //reorder the remaining questions
            int neworder = 0;
            form.Questions.ToList().ForEach(x => x.SortOrder = neworder++);

            _databaseContext.SaveChanges();

            return form.FormId;
        }

        /// <summary>
        /// Add a new option to an existing choice question.
        /// </summary>
        /// <param name="question_id"></param>
        public void addOption(int question_id)
        {
            ChoiceQuestion cq = (ChoiceQuestion)_databaseContext.Questions.Where(q => q.QuestionId == question_id).Single();
            Option o = new Option() { SortOrder = cq.Options.Count, Text = "option goes here" };

            cq.Options.Add(o);
            _databaseContext.SaveChanges();
        }

        /// <summary>
        /// Delete an option from its respective choice question.
        /// </summary>
        /// <param name="option_id"></param>
        /// <returns>Question id from which the option was deleted.</returns>
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

        /// <summary>
        /// Retrieve a question by id.
        /// </summary>
        /// <param name="question_id"></param>
        /// <returns></returns>
        public Question getQuestionById(int question_id)
        {
            return _databaseContext.Questions.Where(q => q.QuestionId == question_id).Single();
        }

        /// <summary>
        /// Add a new form to the database.
        /// </summary>
        /// <param name="form"></param>
        public void addForm(Form form)
        {
            form.DateCreated = DateTime.Now;
            _databaseContext.Forms.AddObject(form);
            _databaseContext.SaveChanges();
        }

        //TODO -- CLEAN THIS UP
        /// <summary>
        /// Update a form and all of its related questions at once.
        /// </summary>
        /// <param name="form"></param>
        /// <param name="question_list">List of questions that are attached to the form, no new ones.</param>
        public void updateForm(Form form, List<Question> question_list)
        {
            Form old_form = _databaseContext.Forms.Where(p => p.FormId == form.FormId).Single();

            old_form.AutoUpdate = form.AutoUpdate;
            old_form.Active = form.Active;
            old_form.Description = form.Description;
            old_form.Name = form.Name;
            old_form.URL = form.URL;
            old_form.KeyField = form.KeyField;
            old_form.Group = form.Group;
            old_form.ListName = form.ListName;
            old_form.DateCreated = form.DateCreated;
            old_form.CreatedBy = form.CreatedBy;
            old_form.FilledByFieldName = form.FilledByFieldName;
            old_form.FilledByFieldType = form.FilledByFieldType;
            old_form.FilledDateFieldName = form.FilledDateFieldName;
            old_form.FilledDateFieldType = form.FilledDateFieldType;

            //For each question, depending on its type, update its copy in the database.
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
                        cq.Multiple = q2.Multiple;

                        //update the text of each option in the choice question, funny way to do it
                        q2.Options.ToList().ForEach(qoption => cq.Options.Where(cqoption => cqoption.OptionId == qoption.OptionId).Single().Text = qoption.Text);

                        old_form.Questions.Attach(cq);
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

                        //update the text of each label in the likert scale question, funny way to do it.
                        q2.Labels.ToList().ForEach(qlabel => lq.Labels.Where(cqlabel => cqlabel.LabelId == qlabel.LabelId).Single().Text = qlabel.Text);

                        old_form.Questions.Attach(lq);
                    }
                }
            _databaseContext.SaveChanges();
        }

        /// <summary>
        /// Copy a form using reflection.
        /// Also changes the CreatedBy/CreatedDate/Name.
        /// </summary>
        /// <param name="old_form">Form you want to copy.</param>
        /// <param name="user_id">User who copied the form (changes createdby)</param>
        /// <returns>New copy of form.</returns>
        public Form copyForm(Form old_form, int user_id)
        {
            //Copy the form
            Form new_form = HOSTCopy.CopyEntity(_databaseContext, old_form, false);
            _databaseContext.Forms.AddObject(new_form);

            //Copy each question, each type has to be handled seperatly.
            foreach (Question old_q in old_form.Questions)
            {
                if (old_q is ChoiceQuestion)
                {
                    ChoiceQuestion new_q = HOSTCopy.CopyEntity(_databaseContext, (ChoiceQuestion)old_q, false);
                    new_form.Questions.Add(new_q);

                    foreach (Option old_option in ((ChoiceQuestion)old_q).Options)
                    {
                        Option new_option = HOSTCopy.CopyEntity(_databaseContext, old_option, true);
                        new_q.Options.Add(new_option);
                    }
                }
                else if (old_q is LikertScaleQuestion)
                {
                    LikertScaleQuestion new_q = HOSTCopy.CopyEntity(_databaseContext, (LikertScaleQuestion)old_q, false);
                    new_form.Questions.Add(new_q);

                    foreach (Label old_label in ((LikertScaleQuestion)old_q).Labels)
                    {
                        Label new_label = HOSTCopy.CopyEntity(_databaseContext, old_label, true);
                        new_q.Labels.Add(new_label);
                    }
                }
                else if (old_q is TextQuestion)
                {
                    TextQuestion new_q = HOSTCopy.CopyEntity(_databaseContext, (TextQuestion)old_q, false);
                    new_form.Questions.Add(new_q);
                }
            }

            new_form.CreatedBy = user_id;
            new_form.DateCreated = DateTime.Now;
            new_form.Name = "Copy - " + new_form.Name;

            _databaseContext.SaveChanges();

            return new_form;
        }

        /// <summary>
        /// Move a question to a new position, then reorder the list to address the shift.
        /// </summary>
        /// <param name="question_id">Question your moving.</param>
        /// <param name="start_position">Position the question is moving from.</param>
        /// <param name="end_position">Position the question is moving to.</param>
        public void changeQuestionPosition(int question_id, int start_position, int end_position)
        {
            int count = 0;

            //Question that' being moved
            Question question = _databaseContext.Questions.Where(q => q.QuestionId == question_id).Single();

            //Form where questions are being reordered
            Form form = question.Form;

            //Shift everything above the target location by one
            if (start_position < end_position)
                form.Questions.Where(q => q.SortOrder <= end_position).ToList().ForEach(q => q.SortOrder = q.SortOrder - 1);
            else if (start_position > end_position )
                form.Questions.Where(q => q.SortOrder >= end_position).ToList().ForEach(q => q.SortOrder = q.SortOrder + 1);

            //Move our question to its new spot
            question.SortOrder = end_position;

            //Renumber all the questions so there are no gaps in number
            form.Questions.OrderBy(q => q.SortOrder).ToList().ForEach(q => q.SortOrder = count++);

            _databaseContext.SaveChanges();
        }
    }
}