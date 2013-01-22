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
            int count = 5;

            Assert.AreEqual(count, formRepository.GetAll().Count());
        }

        [Test]
        public void Can_Get_Form_By_ID()
        {
            int id = 4;

            Assert.AreEqual(id, formRepository.getFormById(id).FormId);
        }

        [Test]
        public void Can_Update_Form_From_Existing_Form()
        {
            Form form;
            int id = 4;
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
            string content = "New Content";
            Form form = new Form() { AutoUpdate = false, Content = content, DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Deleted = false, Description = "This is my description", FormId = 4, Name = "This is my name" };

            formRepository.updateForm(form);

            Form form2 = formRepository.getFormById(4);

            Assert.AreEqual(content, form2.Content);
        }

        [Test]
        public void Can_Delete_Form()
        {
            Form form;
            int id = 3;

            formRepository.deleteFormById(id);
            form = formRepository.getFormById(id);


            Assert.AreEqual(true, form.Deleted);
        }

        [TearDown]
        public void TearDown()
        {

            scope.Dispose();
        }
    }
}


