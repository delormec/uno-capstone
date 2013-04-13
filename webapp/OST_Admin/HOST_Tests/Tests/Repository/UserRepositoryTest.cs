using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using HOST_Admin.Models;
using NSubstitute;
using HOST_Admin.Models.Repository;
using System.Transactions;

namespace HOST_Tests.Tests.Repository
{
    /// <summary>
    /// Set of tests against the UserRepository
    /// </summary>
    [TestFixture]
    class UserRepositoryTest
    {
        IHOSTDataContext HOSTDataContext;
        UserRepository userRepository;
        TransactionScope scope;

        [SetUp]
        public void SetUp()
        {
            scope = new TransactionScope();

            HOSTDataContext = new HOSTDataContext();
            userRepository = new UserRepository(HOSTDataContext);
        }


        /// <summary>
        /// Verify you can add a User.
        /// </summary>
        [Test]
        public void Can_Add_User()
        {
            User user = new User() { UserName = "user101", Password = "pass101", Active = true };

            int user_count = userRepository.getAllUsers().Count();

            //add a user
            userRepository.addUser(user, 1);

            int user_count2 = userRepository.getAllUsers().Count();

            Assert.AreEqual(user_count + 1, user_count2);
        }

        /// <summary>
        /// Verify we can authenticate users.
        /// Tests success and failure.
        /// </summary>
        [Test]
        public void Can_Authenticate_User()
        {
            //Authenticate the admin user w/ 'pass' password (returns user_id -- 1)
            Assert.AreEqual(1, userRepository.authenticateUser("admin", "pass"));
            Assert.AreEqual(-1, userRepository.authenticateUser("not a user", "not a password"));
        }

        /// <summary>
        /// Verify can delete users.
        /// </summary>
        [Test]
        public void Can_Delete_User()
        {
            //get a user
            User user = userRepository.getUserById(1);

            //delete it
            userRepository.deleteUser(user);

            //get an error trying to get the same user
            Assert.Throws<InvalidOperationException>(delegate { userRepository.deleteUser(user); });
        }

        /// <summary>
        /// Verify you can get all roles.
        /// </summary>
        [Test]
        public void Can_Get_All_Roles()
        {
            //is a role
            Assert.IsTrue(userRepository.getAllRoles().First() is Role);

            //user count
            Assert.AreEqual(2, userRepository.getAllRoles().Count());
        }

        /// <summary>
        /// Verify can get all users.
        /// </summary>
        [Test]
        public void Can_Get_All_Users()
        {
            Assert.IsTrue(userRepository.getAllUsers().First() is User);

            Assert.AreEqual(1, userRepository.getAllUsers().Count());
        }

        /// <summary>
        /// Verify you can get the __UserRole session variable
        /// </summary>
        [Test]
        public void Can_Get_Logged_In_Role()
        {
            //Can't test this because it uses HttpContext.Current.Session which is only accessable when webpage is running
        }

        /// <summary>
        /// Verify you can set the __UserRole session variable
        /// </summary>
        [Test]
        public void Can_Set_Logged_In_Role()
        {
            //Can't test this because it uses HttpContext.Current.Session which is only accessable when webpage is running
        }

        /// <summary>
        /// Verify you can get the __UserId session variable
        /// </summary>
        [Test]
        public void Can_Get_Logged_In_User_Id()
        {
            //Can't test this because it uses HttpContext.Current.Session which is only accessable when webpage is running
        }

        /// <summary>
        /// Verify you can set the __UserId session variable
        /// </summary>
        [Test]
        public void Can_Set_Logged_In_User_Id()
        {
            //Can't test this because it uses HttpContext.Current.Session which is only accessable when webpage is running
        }

        /// <summary>
        /// Verify can get a role by role_id
        /// </summary>
        [Test]
        public void Can_Get_Role_By_Id()
        {
            Assert.AreEqual("Administrator", userRepository.getRoleById(1).Name);
            Assert.AreEqual("User", userRepository.getRoleById(2).Name);
        }


        /// <summary>
        /// Verify can get role by user_id
        /// </summary>
        [Test]
        public void Can_Get_Role_By_User_Id()
        {
            Assert.AreEqual("Administrator", userRepository.getRoleByUserId(1).Name);
        }

        /// <summary>
        /// Verify can get user by user_id
        /// </summary>
        [Test]
        public void Can_Get_User_By_Id()
        {
            Assert.AreEqual("admin", userRepository.getUserById(1).UserName);
        }
       
        /// <summary>
        /// Verify you can update a user.
        /// </summary>
        [Test]
        public void Can_Update_User()
        {
            User user = userRepository.getUserById(1);
            
            //update user
            user.UserName = "test";

            userRepository.updateUser(user, 2);

            //verify role updated
            Assert.AreEqual("User", userRepository.getUserById(1).Role.Name);

            Assert.AreEqual("test", userRepository.getUserById(1).UserName);
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
