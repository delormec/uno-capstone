﻿//------------------------------------------------------------------------------
// <auto-generated>
//    This code was generated from a template.
//
//    Manual changes to this file may cause unexpected behavior in your application.
//    Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

using System;
using System.Data.Objects;
using System.Data.Objects.DataClasses;
using System.Data.EntityClient;
using System.ComponentModel;
using System.Xml.Serialization;
using System.Runtime.Serialization;

[assembly: EdmSchemaAttribute()]

namespace Capstone
{
    #region Contexts
    
    /// <summary>
    /// No Metadata Documentation available.
    /// </summary>
    public partial class capstoneDBEntities : ObjectContext
    {
        #region Constructors
    
        /// <summary>
        /// Initializes a new capstoneDBEntities object using the connection string found in the 'capstoneDBEntities' section of the application configuration file.
        /// </summary>
        public capstoneDBEntities() : base("name=capstoneDBEntities", "capstoneDBEntities")
        {
            this.ContextOptions.LazyLoadingEnabled = true;
            OnContextCreated();
        }
    
        /// <summary>
        /// Initialize a new capstoneDBEntities object.
        /// </summary>
        public capstoneDBEntities(string connectionString) : base(connectionString, "capstoneDBEntities")
        {
            this.ContextOptions.LazyLoadingEnabled = true;
            OnContextCreated();
        }
    
        /// <summary>
        /// Initialize a new capstoneDBEntities object.
        /// </summary>
        public capstoneDBEntities(EntityConnection connection) : base(connection, "capstoneDBEntities")
        {
            this.ContextOptions.LazyLoadingEnabled = true;
            OnContextCreated();
        }
    
        #endregion
    
        #region Partial Methods
    
        partial void OnContextCreated();
    
        #endregion
    
        #region ObjectSet Properties
    
        /// <summary>
        /// No Metadata Documentation available.
        /// </summary>
        public ObjectSet<Form> Forms
        {
            get
            {
                if ((_Forms == null))
                {
                    _Forms = base.CreateObjectSet<Form>("Forms");
                }
                return _Forms;
            }
        }
        private ObjectSet<Form> _Forms;

        #endregion
        #region AddTo Methods
    
        /// <summary>
        /// Deprecated Method for adding a new object to the Forms EntitySet. Consider using the .Add method of the associated ObjectSet&lt;T&gt; property instead.
        /// </summary>
        public void AddToForms(Form form)
        {
            base.AddObject("Forms", form);
        }

        #endregion
    }
    

    #endregion
    
    #region Entities
    
    /// <summary>
    /// No Metadata Documentation available.
    /// </summary>
    [EdmEntityTypeAttribute(NamespaceName="capstoneModel", Name="Form")]
    [Serializable()]
    [DataContractAttribute(IsReference=true)]
    public partial class Form : EntityObject
    {
        #region Factory Method
    
        /// <summary>
        /// Create a new Form object.
        /// </summary>
        /// <param name="formId">Initial value of the FormId property.</param>
        public static Form CreateForm(global::System.Int32 formId)
        {
            Form form = new Form();
            form.FormId = formId;
            return form;
        }

        #endregion
        #region Primitive Properties
    
        /// <summary>
        /// No Metadata Documentation available.
        /// </summary>
        [EdmScalarPropertyAttribute(EntityKeyProperty=true, IsNullable=false)]
        [DataMemberAttribute()]
        public global::System.Int32 FormId
        {
            get
            {
                return _FormId;
            }
            set
            {
                if (_FormId != value)
                {
                    OnFormIdChanging(value);
                    ReportPropertyChanging("FormId");
                    _FormId = StructuralObject.SetValidValue(value);
                    ReportPropertyChanged("FormId");
                    OnFormIdChanged();
                }
            }
        }
        private global::System.Int32 _FormId;
        partial void OnFormIdChanging(global::System.Int32 value);
        partial void OnFormIdChanged();
    
        /// <summary>
        /// No Metadata Documentation available.
        /// </summary>
        [EdmScalarPropertyAttribute(EntityKeyProperty=false, IsNullable=true)]
        [DataMemberAttribute()]
        public Nullable<global::System.Boolean> AutoUpdate
        {
            get
            {
                return _AutoUpdate;
            }
            set
            {
                OnAutoUpdateChanging(value);
                ReportPropertyChanging("AutoUpdate");
                _AutoUpdate = StructuralObject.SetValidValue(value);
                ReportPropertyChanged("AutoUpdate");
                OnAutoUpdateChanged();
            }
        }
        private Nullable<global::System.Boolean> _AutoUpdate;
        partial void OnAutoUpdateChanging(Nullable<global::System.Boolean> value);
        partial void OnAutoUpdateChanged();
    
        /// <summary>
        /// No Metadata Documentation available.
        /// </summary>
        [EdmScalarPropertyAttribute(EntityKeyProperty=false, IsNullable=false)]
        [DataMemberAttribute()]
        public global::System.Boolean Deleted
        {
            get
            {
                return _Deleted;
            }
            set
            {
                OnDeletedChanging(value);
                ReportPropertyChanging("Deleted");
                _Deleted = StructuralObject.SetValidValue(value);
                ReportPropertyChanged("Deleted");
                OnDeletedChanged();
            }
        }
        private global::System.Boolean _Deleted = false;
        partial void OnDeletedChanging(global::System.Boolean value);
        partial void OnDeletedChanged();
    
        /// <summary>
        /// No Metadata Documentation available.
        /// </summary>
        [EdmScalarPropertyAttribute(EntityKeyProperty=false, IsNullable=true)]
        [DataMemberAttribute()]
        public global::System.String Content
        {
            get
            {
                return _Content;
            }
            set
            {
                OnContentChanging(value);
                ReportPropertyChanging("Content");
                _Content = StructuralObject.SetValidValue(value, true);
                ReportPropertyChanged("Content");
                OnContentChanged();
            }
        }
        private global::System.String _Content;
        partial void OnContentChanging(global::System.String value);
        partial void OnContentChanged();
    
        /// <summary>
        /// No Metadata Documentation available.
        /// </summary>
        [EdmScalarPropertyAttribute(EntityKeyProperty=false, IsNullable=true)]
        [DataMemberAttribute()]
        public global::System.String Name
        {
            get
            {
                return _Name;
            }
            set
            {
                OnNameChanging(value);
                ReportPropertyChanging("Name");
                _Name = StructuralObject.SetValidValue(value, true);
                ReportPropertyChanged("Name");
                OnNameChanged();
            }
        }
        private global::System.String _Name;
        partial void OnNameChanging(global::System.String value);
        partial void OnNameChanged();
    
        /// <summary>
        /// No Metadata Documentation available.
        /// </summary>
        [EdmScalarPropertyAttribute(EntityKeyProperty=false, IsNullable=true)]
        [DataMemberAttribute()]
        public global::System.String Description
        {
            get
            {
                return _Description;
            }
            set
            {
                OnDescriptionChanging(value);
                ReportPropertyChanging("Description");
                _Description = StructuralObject.SetValidValue(value, true);
                ReportPropertyChanged("Description");
                OnDescriptionChanged();
            }
        }
        private global::System.String _Description;
        partial void OnDescriptionChanging(global::System.String value);
        partial void OnDescriptionChanged();
    
        /// <summary>
        /// No Metadata Documentation available.
        /// </summary>
        [EdmScalarPropertyAttribute(EntityKeyProperty=false, IsNullable=true)]
        [DataMemberAttribute()]
        public Nullable<global::System.DateTime> DateCreated
        {
            get
            {
                return _DateCreated;
            }
            set
            {
                OnDateCreatedChanging(value);
                ReportPropertyChanging("DateCreated");
                _DateCreated = StructuralObject.SetValidValue(value);
                ReportPropertyChanged("DateCreated");
                OnDateCreatedChanged();
            }
        }
        private Nullable<global::System.DateTime> _DateCreated;
        partial void OnDateCreatedChanging(Nullable<global::System.DateTime> value);
        partial void OnDateCreatedChanged();

        #endregion
    
    }

    #endregion
    
}