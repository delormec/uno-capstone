using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using OST_Admin.Models;

namespace OSTTests.Tests.Controllers
{

    [TestFixture]
    class test1
    {

        [Test]
        public void convert_form_to_xml()
        {
            Form form = new Form();


            FormHelper fh = new FormHelper();

            fh.generateXML(form);



            

        }

        private int addnumbers(int p, int p_2)
        {
            return 12;
        }


    }
}
