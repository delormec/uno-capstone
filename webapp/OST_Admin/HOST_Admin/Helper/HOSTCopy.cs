using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using HOST_Admin.Models;
using System.Data.Objects.DataClasses;
using System.Reflection;

public class HOSTCopy
{
    public static T CopyEntity<T>(HOSTDataContext ctx, T entity,
        bool copyKeys = false) where T : EntityObject
    {
        T clone = ctx.CreateObject<T>();
        PropertyInfo[] pis = entity.GetType().GetProperties();
        //PropertyInfo[] pis = typeof(T).GetType().GetProperties();

        foreach (PropertyInfo pi in pis)
        {
            EdmScalarPropertyAttribute[] attrs = (EdmScalarPropertyAttribute[])
                          pi.GetCustomAttributes(typeof(EdmScalarPropertyAttribute), false);

            foreach (EdmScalarPropertyAttribute attr in attrs)
            {
                if (!copyKeys && attr.EntityKeyProperty)
                    continue;

                pi.SetValue(clone, pi.GetValue(entity, null), null);
            }
        }

        return clone;
    }
}
