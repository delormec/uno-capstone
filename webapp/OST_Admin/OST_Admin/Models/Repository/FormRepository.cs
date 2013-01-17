using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Microsoft.Data.OData;
using System.Web.Http;

namespace OST_Admin.Models.Repository
{
    public class FormRepository : IFormRepository
    {
        OSTDBEntities databaseContext;

        public FormRepository()
        {
            databaseContext = new OSTDBEntities();
        }

       
        public System.Linq.IQueryable<Form> GetAll()
        {
            return databaseContext.Forms;
        }


        public Form getForm(int id)
        {
            return databaseContext.Forms.Where(p => p.FormId == id).Single();
        }
    }
}