using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using NUnit.Framework;

using NSubstitute;
using OST_Admin.Models;
using OST_Admin.Models.Repository;
using System.Data.Objects.DataClasses;
using OSTTests.Tests.Controllers;
using System.Transactions;

namespace OST_Admin.Tests.Controllers
{
    [TestFixture]
    public class FormRepositoryTest
    {
        //var OSTDataContext;
        TransactionScope scope;
        IOSTDataContext _OSTDataContext;
        FormRepository formRepository;


        [SetUp]
        public void SetUp()
        {
            scope = new TransactionScope();
            //_OSTDataContext = Substitute.For<IOSTDataContext>();
            _OSTDataContext = new OSTDataContext();
            formRepository = new FormRepository(_OSTDataContext);
            //_OSTDataContext.Forms

            //_OSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false, Content = "This is my content", DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Deleted = false, Description = "This is my description",  Name = "This is my name", Questions = new EntityCollection<Question>() { new TextQuestion() { HelpText = "This is my help text bro", Text = "This is my question?" } } });
            //_OSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false, Content = "This is my content", DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Deleted = false, Description = "This is my description",  Name = "This is my name", Questions = new EntityCollection<Question>() { new TextQuestion() { HelpText = "This is my help text bro", Text = "This is my question?" } } });
            //_OSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false, Content = "This is my content", DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Deleted = false, Description = "This is my description",  Name = "This is my name", Questions = new EntityCollection<Question>() { new TextQuestion() { HelpText = "This is my help text bro", Text = "This is my question?" } } });
            //_OSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false, Content = "This is my content", DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Deleted = false, Description = "This is my description",  Name = "This is my name", Questions = new EntityCollection<Question>() { new TextQuestion() { HelpText = "This is my help text bro", Text = "This is my question?" } } });
            //_OSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false, Content = "This is my content", DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Deleted = false, Description = "This is my description",  Name = "This is my name", Questions = new EntityCollection<Question>() { new TextQuestion() { HelpText = "This is my help text bro", Text = "This is my question?" } } });



            //Form formy = new Form();

            //formy.AutoUpdate = false;
            //formy.Content = "This is my content";
            //formy.DateCreated = new DateTime(2010, 10, 10, 10, 10, 10);
            //formy.Deleted = false;
            //formy.Description = "This is my description";
            //formy.Name = "This is my name";

            //_OSTDataContext.Forms.AddObject(formy);


            //formy.Questions.Add(new TextQuestion() { HelpText = "This is my help text bro", Text = "This is my question?" });

            //_OSTDataContext.SaveChanges();

            //Form formy2 = OSTDataContext.Forms.Where(p => p.FormId == 1).Single();


            

            //EntityCollection<Question> questions = new EntityCollection<Question>();
            //Question q = new TextQuestion() { FormFormId = 1, HelpText = "This is my help text bro", QuestionId = 1, Text = "This is my question?" };

            //questions.Add(q);

            //formy2.Questions = questions;

            

            //questions.

            //formy.Questions.Add(new TextQuestion { Form = formy, FormFormId = 1, HelpText = "This is my help text bro", QuestionId = 1, Text = "This is my question?" });

            //formy.Questions.

            //OSTDataContext.Forms.AddObject(formy);

            //OSTDataContext.SaveChanges();
        }

        [Test]
        public void Can_Get_All_Forms()
        {
            int count = 6;

            Assert.AreEqual(count, formRepository.GetAll().Count());
        }

        [Test]
        public void Can_Get_Form_By_ID()
        {
            int id = 1;

            Assert.AreEqual(id, formRepository.getFormById(id).FormId);
        }

        [Test]
        public void Can_Update_Form_From_Existing_Form()
        {
            Form form;
            int id = 26;
            string name = "Test Form";

            form = formRepository.getFormById(id);

            form.Name = name;
            formRepository.updateForm(form);

            form = formRepository.getFormById(id);

            Assert.AreEqual(name, form.Name);
        }

        [Test]
        public void Can_Update_Form_With_New_Form()
        {
            string name = "New Content";
            int id = 25;
            Form form = new Form() { AutoUpdate = false, DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Deleted = false, Description = "This is my description", FormId = id, Name = name };

            formRepository.updateForm(form);

            Form form2 = formRepository.getFormById(id);

            Assert.AreEqual(name, form2.Name);
        }

        [Test]
        public void Can_Delete_Form()
        {
            Form form;
            int id = 1;

            formRepository.deleteFormById(id);
            form = formRepository.getFormById(id);


            Assert.AreEqual(true, form.Deleted);
        }

        [Test]
        public void Can_Add_Question()
        {
            int id = 1;

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

        [Test]
        public void Can_Delete_Question()
        {
            int id = 1;

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
            //Assert.IsNull(question);
            //Assert.Throws<InvalidOperationException>(delegate { formRepository.addQuestion(1251, "Text"); });
        }


        [Test]
        public void Can_Add_Form()
        {
            Form form = new Form();

            int form_count = formRepository.GetAll().Count();

            formRepository.addForm(form);


            Assert.AreEqual(form_count + 1, formRepository.GetAll().Count());
        }


        [TearDown]
        public void TearDown()
        {

            scope.Dispose();
        }
    }
}


