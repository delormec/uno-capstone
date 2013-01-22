using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using OST_Admin.Models;
using NSubstitute;

namespace OSTTests.Tests.Controllers
{
    [TestFixture]
    class UserRepositoryTest
    {
        IOSTDataContext OSTDataContext;
        UserRepository userRepository;

        [SetUp]
        public void SetUp()
        {
            OSTDataContext = Substitute.For<IOSTDataContext>();
            userRepository = new UserRepository(OSTDataContext);

            //OSTDataContext.User.

            OSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false, Content = "This is my content", DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Deleted = false, Description = "This is my description", FormId = 1, Name = "This is my name" });
            OSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false, Content = "This is my content", DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Deleted = false, Description = "This is my description", FormId = 2, Name = "This is my name" });
            OSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false, Content = "This is my content", DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Deleted = false, Description = "This is my description", FormId = 3, Name = "This is my name" });
            OSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false, Content = "This is my content", DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Deleted = false, Description = "This is my description", FormId = 4, Name = "This is my name" });
            OSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false, Content = "This is my content", DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Deleted = false, Description = "This is my description", FormId = 5, Name = "This is my name" });
            OSTDataContext.SaveChanges();
        }
    }
}
