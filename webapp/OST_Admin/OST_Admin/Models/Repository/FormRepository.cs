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
        OSTDataContext _databaseContext;

        public FormRepository(IOSTDataContext databaseContext)
        {
            _databaseContext = new OSTDataContext();
        }

       
        public System.Linq.IQueryable<Form> GetAll()
        {
            return _databaseContext.Forms;
        }

        public Form getFormById(int id)
        {
            return _databaseContext.Forms.Where(p => p.FormId == id).Single();
        }


        public void updateForm(Form form)
        {
            Form old_form;

            old_form = _databaseContext.Forms.Where(p => p.FormId == form.FormId).Single();

            old_form.AutoUpdate = form.AutoUpdate;
            old_form.Content = form.Content;
            old_form.DateCreated = form.DateCreated;
            old_form.Deleted = form.Deleted;
            old_form.Description = form.Description;
            old_form.Name = form.Name;

            _databaseContext.SaveChanges();
        }

        public void deleteFormById(int id)
        {
            Form form;

            form = getFormById(id);
            form.Deleted = true;

            _databaseContext.SaveChanges();
        }

        public void testy()
        {

        }
    }
}