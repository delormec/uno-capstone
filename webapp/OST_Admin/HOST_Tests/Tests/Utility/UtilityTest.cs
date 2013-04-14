using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using HOST_Admin.Models;
using System.Xml.Linq;
using HOST_Admin.Helper;

namespace HOST_Tests.Tests.Utility
{
    /// <summary>
    /// Set of tests against the UserRepository
    /// </summary>
    [TestFixture]
    class UtilityTest
    {
        /// <summary>
        /// Tests the XML.fromForm and XML.fromFormID
        /// Normally this xml is read by the android app, but we can still parse is using XDocument in C#
        /// </summary>
        [Test]
        public void Can_Serialize_Form_To_XML()
        {
            Form form = new Form() { Name="Test Form", Description="Description", Group="Test Group" };

            String xml = XML.fromForm(form);

            XDocument xmldoc = XDocument.Parse(xml);

            //the xml doc isn't empty
            Assert.IsNotNull(xmldoc);

            Assert.AreEqual("Test Form", xmldoc.Descendants("meta").Elements("name").First().Value);
            Assert.AreEqual("Description", xmldoc.Descendants("meta").Elements("description").First().Value);
            Assert.AreEqual("Test Group", xmldoc.Descendants("meta").Elements("formgroup").First().Value);

            //no questions
            Assert.AreEqual(0, xmldoc.Descendants("questions").Elements("question").Count());

            //Test the by ID version
            xml = XML.fromFormID(2);
            xmldoc = XDocument.Parse(xml);

            //the xml doc isn't empty
            Assert.IsNotNull(xmldoc);

            Assert.AreEqual("Use me to test stuff", xmldoc.Descendants("meta").Elements("name").First().Value);
            Assert.AreEqual("Form Description", xmldoc.Descendants("meta").Elements("description").First().Value);
            Assert.AreEqual("undefined", xmldoc.Descendants("meta").Elements("formgroup").First().Value);

            //88 questions
            Assert.AreEqual(88, xmldoc.Descendants("questions").Elements("question").Count());
        }

        /// <summary>
        /// Since testing this function requires a real connection to pass, we can only test that it fails.
        /// </summary>
        [Test]
        public void Can_Generate_Form_From_SharePoint_SOAP()
        {
            Assert.Throws<ArgumentNullException>(delegate { HOSTSharePoint.generateForm(new HOST_Admin.Models.ViewModel.SharePointSOAPRequestViewModel()); });
        }

        /// <summary>
        /// Verify we can generate random 4-8 byte salts.
        /// </summary>
        [Test]
        public void Can_Generate_Random_Salt()
        {
            byte[] salt = HOSTEncrpyption.getRandomSaltBytes();

            //salt is between 4 and 8 bytes
            Assert.IsTrue(salt.Length >= 4 && salt.Length <= 8);

            HashSet<byte[]> hash = new HashSet<byte[]>();

            //generate 1000 hashes
            for (int i = 0; i < 1000; i++)
            {
                hash.Add(HOSTEncrpyption.getRandomSaltBytes());
            }

            //this can technically fail, since it could randomly duplicate
            //shouldn't duplicate tho since there are many combinations of 4-8 byte
            Assert.AreEqual(1000, hash.Count());
        }

        /// <summary>
        /// Test that we generated a SHA512 hash from a given string and salt.
        /// The algorithm generates SHA12+salt so the length of the string will always be 64+length of the salt
        /// </summary>
        [Test]
        public void Can_Compute_SHA512_Hash()
        {
            byte[] salt = HOSTEncrpyption.getRandomSaltBytes();
            String password = "password";
            String hash = HOSTEncrpyption.ComputeHash(password, salt);


            //test the length is right
            Assert.AreEqual(64 + salt.Length, Convert.FromBase64String(hash).Length);

            //test that the hash is the same each time it is generated
            Assert.AreEqual(HOSTEncrpyption.ComputeHash(password, salt), hash);

            //Assert a different password generates a different hash
            Assert.AreNotEqual(HOSTEncrpyption.ComputeHash("password2", salt), HOSTEncrpyption.ComputeHash("password", salt));
        }

        /// <summary>
        /// Verify a SHA12+salt hash.
        /// </summary>
        [Test]
        public void Can_Verify_Hash()
        {
            //generate a salt
            byte[] salt = HOSTEncrpyption.getRandomSaltBytes();

            //hash a password
            String hash = HOSTEncrpyption.ComputeHash("pass", salt);

            //verify a that hash
            Assert.IsTrue(HOSTEncrpyption.VerifyHash("pass", hash));

            //verify a bad password fails the verification
            Assert.IsFalse(HOSTEncrpyption.VerifyHash("pazz", hash));
        }


    }
}
