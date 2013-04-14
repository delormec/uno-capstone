using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using System.Transactions;
using HOST_Admin.Models;
using HOST_Admin.Controllers;
using HOST_Admin.Models.Repository;

namespace HOST_Tests.Tests.Controllers
{
    /// <summary>
    /// Set of tests against the AccountController
    /// </summary>
    [TestFixture]
    class AccountControllerTest
    {
        TransactionScope scope;
        UserRepository userRepository;
        IHOSTDataContext HOSTDataContext;
        AccountController accountController;

        /// <summary>
        /// Doesn't seem anything meaningful can be obtained out of testing the controller.
        /// MVC architecture doesn't really lend itself.
        /// </summary>
        [SetUp]
        public void SetUp()
        {
            scope = new TransactionScope();
            HOSTDataContext = new HOSTDataContext();
            userRepository = new UserRepository(HOSTDataContext);
            accountController = new AccountController(userRepository);
        }


        [Test]
        public void Can_Create_LogIn_Page()
        {
            String user_name = "test";
            String password = "password";

            System.Web.Mvc.ActionResult actionResult =  accountController.LogIn(user_name, false);
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
