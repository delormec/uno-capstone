using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Microsoft.Data.OData;
using System.Web.Http;

namespace Capstone.Models.Repository
{
    public class FormRepository : IFormRepository
    {
        capstoneDBEntities databaseContext;

        public FormRepository()
        {
            databaseContext = new capstoneDBEntities();
        }

       
        public System.Linq.IQueryable<Form> GetAll()
        {
            return databaseContext.Forms;
        }
    }
}