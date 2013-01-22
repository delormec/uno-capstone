using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace OSTTests.Tests.Controllers
{
    class UserRepository
    {
        private OST_Admin.Models.IOSTDataContext OSTDataContext;

        public UserRepository(OST_Admin.Models.IOSTDataContext OSTDataContext)
        {
            // TODO: Complete member initialization
            this.OSTDataContext = OSTDataContext;
        }
    }
}
