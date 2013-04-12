using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using HOST_Admin.Models;
using NSubstitute;

namespace OSTTests.Tests.Controllers
{
    [TestFixture]
    class UserRepositoryTest
    {
        IHOSTDataContext HOSTDataContext;
        UserRepository userRepository;

        [SetUp]
        public void SetUp()
        {
            HOSTDataContext = Substitute.For<IHOSTDataContext>();
            userRepository = new UserRepository(HOSTDataContext);

            //HOSTDataContext.User.

            HOSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false,  DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Active = false, Description = "This is my description", FormId = 1, Name = "This is my name" });
            HOSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false, DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Active = false, Description = "This is my description", FormId = 2, Name = "This is my name" });
            HOSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false, DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Active = false, Description = "This is my description", FormId = 3, Name = "This is my name" });
            HOSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false, DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Active = false, Description = "This is my description", FormId = 4, Name = "This is my name" });
            HOSTDataContext.Forms.AddObject(new Form() { AutoUpdate = false, DateCreated = new DateTime(2010, 10, 10, 10, 10, 10), Active = false, Description = "This is my description", FormId = 5, Name = "This is my name" });
            HOSTDataContext.SaveChanges();
        }
    }
}
