using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace OSTTests.Tests.Controllers
{
    class UserRepository
    {
        private HOST_Admin.Models.IHOSTDataContext HOSTDataContext;

        public UserRepository(HOST_Admin.Models.IHOSTDataContext HOSTDataContext)
        {
            // TODO: Complete member initialization
            this.HOSTDataContext = HOSTDataContext;
        }
    }
}
