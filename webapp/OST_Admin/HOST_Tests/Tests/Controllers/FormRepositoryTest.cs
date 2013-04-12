using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using NUnit.Framework;

using NSubstitute;
using HOST_Admin.Models;
using HOST_Admin.Models.Repository;
using System.Data.Objects.DataClasses;
using OSTTests.Tests.Controllers;
using System.Transactions;
using HOST_Admin.Models.ViewModel;

namespace HOST_Admin.Tests.Controllers
{
    /// <summary>
    /// Set of tests against the FormRepository
    /// </summary>
    [TestFixture]
    public class FormRepositoryTest
    {
        //var HOSTDataContext;
        TransactionScope scope;
        IHOSTDataContext _HOSTDataContext;
        FormRepository formRepository;

        /// <summary>
        /// Set up function, starts a transaction scope up.
        /// Runes before a test begins.
        /// All changes made to the test database are rolled back after this code runs.
        /// </summary>
        [SetUp]
        public void SetUp()
        {
            scope = new TransactionScope();
            _HOSTDataContext = new HOSTDataContext();
            formRepository = new FormRepository(_HOSTDataContext);
        }

        /// <summary>
        /// Test that we can get all forms in the database.
        /// </summary>
        [Test]
        public void Can_Get_All_Forms()
        {
            int count = 8;

            Assert.AreEqual(count, formRepository.getAll().Count());
        }

        /// <summary>
        /// Test that we can get a form by id.
        /// </summary>
        [Test]
        public void Can_Get_Form_By_Id()
        {
            int id = 2;

            Assert.AreEqual(id, formRepository.getFormById(id).FormId);
        }

        /// <summary>
        /// Test that we can update a form's meta data and even question data.
        /// </summary>
        [Test]
        public void Can_Update_Form()
        {
            Form form;
            int id = 12;

            //new name
            string name = "Test Form";
            string question_text = "What's up with this question?";

            //get a form + list of questions out of db
            form = formRepository.getFormById(id);
            List<Question> questions = form.Questions.ToList();

            //change some values
            form.Name = name;
            questions[0].Text = question_text;

            //save our changes to the database
            formRepository.updateForm(form, questions);

            //Pull out the form we just inserted and assert.
            Form form2 = formRepository.getFormById(id);

            Assert.AreEqual(name, form.Name);
            Assert.AreEqual(question_text, form.Questions.ToList()[0].Text);
        }

        /// <summary>
        /// Verify you can copy a form and that they contain the same questions/data
        /// </summary>
        [Test]
        public void Can_Copy_Form()
        {
            int id = 2;

            Form form = formRepository.getFormById(id);

            //copy the form
            Form form2 = formRepository.copyForm(form, 2);


            Assert.AreEqual(form.Questions.Count(), form2.Questions.Count());
            Assert.AreEqual(form.URL, form2.URL);
            Assert.AreEqual("Copy - " + form.Name, form2.Name);

            //should not be equal
            Assert.AreNotEqual(form.FormId, form2.FormId);
            Assert.AreNotEqual(form.DateCreated, form2.DateCreated);
        }

        /// <summary>
        /// Verify we can add a new form the database.
        /// </summary>
        [Test]
        public void Can_Add_Form()
        {
            Form form = new Form();

            int form_count = formRepository.getAll().Count();

            formRepository.addForm(form);

            Assert.AreEqual(form_count + 1, formRepository.getAll().Count());
        }

        /// <summary>
        /// Test that we can delete a form.
        /// Test involves assuring that an error is thrown when we try to get a Form that we just deleted.
        /// </summary>
        [Test]
        public void Can_Delete_Form()
        {
            int id = 2;

            formRepository.deleteFormById(id);

            Assert.Throws<InvalidOperationException>(delegate { formRepository.getFormById(id); });
        }

        /// <summary>
        /// Tests that we can add each type of questions.
        /// Also tests exceptions that can be thrown if you pass in bad question types or invalid form id's.
        /// </summary>
        [Test]
        public void Can_Add_Question()
        {
            int id = 2;

            //find out how many questions are on my form
            int question_count = formRepository.getFormById(id).Questions.Count();

            //add each type of question and verify theres a new question in the form
            formRepository.addQuestion(id, "Likert");
            Assert.AreEqual(question_count+1, formRepository.getFormById(id).Questions.Count());
            formRepository.addQuestion(id, "Text");
            Assert.AreEqual(question_count + 2, formRepository.getFormById(id).Questions.Count());
            formRepository.addQuestion(id, "Choice");
            Assert.AreEqual(question_count + 3, formRepository.getFormById(id).Questions.Count());

            //two types of errors that can occur -- bad question ID and bad question type
            Assert.Throws<InvalidOperationException>( delegate{ formRepository.addQuestion(1251, "Text");});
            Assert.Throws<ArgumentException>(delegate { formRepository.addQuestion(id, "SUP BRO"); });

            //verify that the above didn't actually save any questions
            Assert.AreEqual(question_count + 3, formRepository.getFormById(id).Questions.Count());
        }

        /// <summary>
        /// Verify you can get a question by question_id.
        /// </summary>
        [Test]
        public void Can_Get_Question_By_Id()
        {
            int id = 10;
            Assert.AreEqual(id, formRepository.getQuestionById(id).QuestionId);
        }

        /// <summary>
        /// Verify we can delete a question, and that exception is thrown when you try to delete an invalid question_id.
        /// </summary>
        [Test]
        public void Can_Delete_Question()
        {
            int id = 2;

            //get a question id
            Form form = formRepository.getFormById(id);

            //Count how many questions I have
            int question_count = form.Questions.Count();

            //get a question
            Question question = form.Questions.First();

            //delete a question
            formRepository.deleteQuestion(question.QuestionId);

            //verify its gone
            Assert.AreEqual(question_count - 1, form.Questions.Count());

            //verify an error is thrown if we try to delete the same ID again
            Assert.Throws<InvalidOperationException>(delegate { formRepository.deleteQuestion(question.QuestionId); });
        }

        /// <summary>
        /// Verify you can reorder a question (update its sort order).
        /// </summary>
        [Test]
        public void Can_Update_Question_Position()
        {
            int end_position = 5;
            int question_id = 1;

            //get a question
            Question question = formRepository.getQuestionById(question_id);

            int start_position = question.SortOrder;

            //change the position
            formRepository.changeQuestionPosition(question_id, start_position, end_position);

            //get the same question back
            Question question2 = formRepository.getQuestionById(question_id);

            //verify its in its new position
            Assert.AreEqual(end_position, question2.SortOrder);
        }

        /// <summary>
        /// Verify we can add an option to a choice question, check for error when it's not a choice question.
        /// </summary>
        [Test]
        public void Can_Add_Option()
        {
            int id = 2;

            Form form = formRepository.getFormById(id);

            //find a ChoiceQuestion in the form we just pulled up
            ChoiceQuestion cq = form.Questions.OfType<ChoiceQuestion>().First();

            //count number of options
            int option_count = cq.Options.Count();

            //add an option
            formRepository.addOption(cq.QuestionId);

            //check it got added
            Assert.AreEqual(option_count+1, cq.Options.Count());

            //find a TextQuestion in the form we just pulled up
            TextQuestion tq = form.Questions.OfType<TextQuestion>().First();

            //verify error thrown when you add to a non-choice question
            Assert.Throws<InvalidCastException>(delegate { formRepository.addOption(tq.QuestionId); });
        }
        
        /// <summary>
        /// Verify we can delete an option from a choice question.
        /// </summary>
        [Test]
        public void Can_Delete_Option()
        {
            int id = 2;

            Form form = formRepository.getFormById(id);

            //find a ChoiceQuestion in the form we just pulled up
            ChoiceQuestion cq = form.Questions.OfType<ChoiceQuestion>().First();

            //count number of options
            int option_count = cq.Options.Count();

            formRepository.deleteOption(cq.Options.First().OptionId);

            //check it got deleted
            Assert.AreEqual(option_count - 1, cq.Options.Count());

            //find a TextQuestion in the form we just pulled up
            TextQuestion tq = form.Questions.OfType<TextQuestion>().First();
        }

        /// <summary>
        /// This is kind of a pain to test.
        /// Verify a form is generated from a sharepoint SOAPResponse.
        /// </summary>
        [Test]
        public void Can_Create_Form_From_SharePoint()
        {
            //Create some input
            Form form = formRepository.createFormFromSharePoint(new List<SharePointSOAPResponseViewModel>() {
                                                                    new SharePointSOAPResponseViewModel("field_name", "Choice")
                                                                        { choices = new List<String>(){"test", "test2", "test3"}},
                                                                    new SharePointSOAPResponseViewModel("field_name2", "Currency"),
                                                                    new SharePointSOAPResponseViewModel("field_name3", "DateTime"),
                                                                    new SharePointSOAPResponseViewModel("field_name4", "Note"),
                                                                    new SharePointSOAPResponseViewModel("field_name5", "RandomDataType")
                                                                }, 2);

            //we have 5 questions
            Assert.AreEqual(5 ,form.Questions.Count());

            //first is a choice question
            Assert.IsTrue(form.Questions.ToList()[0] is ChoiceQuestion);
            //and has 3 choices
            Assert.AreEqual(3, form.Questions.OfType<ChoiceQuestion>().First().Options.Count);
            //and is of type SINGLE
            Assert.AreEqual("SINGLE", form.Questions.ToList()[0].FieldType);

            //the rest are text questions with varying field_types
            Assert.IsTrue(form.Questions.ToList()[1] is TextQuestion);
            Assert.AreEqual("NUMBER", form.Questions.ToList()[1].FieldType);

            Assert.IsTrue(form.Questions.ToList()[2] is TextQuestion);
            Assert.AreEqual("DATE", form.Questions.ToList()[2].FieldType);

            Assert.IsTrue(form.Questions.ToList()[3] is TextQuestion);
            Assert.AreEqual("MULTI", form.Questions.ToList()[3].FieldType);

            Assert.IsTrue(form.Questions.ToList()[4] is TextQuestion);
            Assert.AreEqual("SINGLE", form.Questions.ToList()[4].FieldType);
        }

        /// <summary>
        /// Tear down function, called when test ends. 
        /// Rolls back all changes that were made to database.
        /// </summary>
        [TearDown]
        public void TearDown()
        {
            scope.Dispose();
        }
    }
}


