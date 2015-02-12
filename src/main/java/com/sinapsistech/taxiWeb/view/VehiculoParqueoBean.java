package com.sinapsistech.taxiWeb.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.sinapsistech.taxiWeb.model.Departamento;
import com.sinapsistech.taxiWeb.model.VehiculoParqueo;
import com.sinapsistech.taxiWeb.model.Compania;
import com.sinapsistech.taxiWeb.model.Parqueo;
import com.sinapsistech.taxiWeb.model.Vehiculo;

/**
 * Backing bean for VehiculoParqueo entities.
 * <p/>
 * This class provides CRUD functionality for all VehiculoParqueo entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class VehiculoParqueoBean implements Serializable
{

   private static final long serialVersionUID = 1L;
   FacesContext context = FacesContext.getCurrentInstance();
   ExternalContext externalContext = context.getExternalContext();
   HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

   /*
    * Support creating and retrieving VehiculoParqueo entities
    */

   private Integer id;

   public Integer getId()
   {
      return this.id;
   }

   public void setId(Integer id)
   {
      this.id = id;
   }

   private VehiculoParqueo vehiculoParqueo;

   public VehiculoParqueo getVehiculoParqueo()
   {
      return this.vehiculoParqueo;
   }

   public void setVehiculoParqueo(VehiculoParqueo vehiculoParqueo)
   {
      this.vehiculoParqueo = vehiculoParqueo;
   }

   @Inject
   private Conversation conversation;

   @PersistenceContext(unitName = "taxiWeb-persistence-unit", type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   public String create()
   {

      this.conversation.begin();
      this.conversation.setTimeout(1800000L);
      return "create?faces-redirect=true";
   }

   public void retrieve()
   {

      if (FacesContext.getCurrentInstance().isPostback())
      {
         return;
      }

      if (this.conversation.isTransient())
      {
         this.conversation.begin();
         this.conversation.setTimeout(1800000L);
      }

      if (this.id == null)
      {
         this.vehiculoParqueo = this.example;
      }
      else
      {
         this.vehiculoParqueo = findById(getId());
      }
   }

   public VehiculoParqueo findById(Integer id)
   {

      return this.entityManager.find(VehiculoParqueo.class, id);
   }

   /*
    * Support updating and deleting VehiculoParqueo entities
    */

   public String update()
   {
       System.out.println("Agarrando el contexto.");
       String nombre = request.getUserPrincipal().getName();
	   
	   this.conversation.end();

      try
      {
         if (this.id == null)
         {
        	 System.out.println("Entro por vehiculoParqueo nuevo");
        	 
        	 this.vehiculoParqueo.setFechaIngreso(new Date());
        	 this.vehiculoParqueo.setUsuarioReg(nombre);
        	 this.vehiculoParqueo.setFlagEstado("AC");
        	 this.vehiculoParqueo.setFechaIngreso(new Date());
        	 this.entityManager.persist(this.vehiculoParqueo);
        	 return "search?faces-redirect=true";
         }
         else
         {
         	System.out.println("Entro por vehiculoParqueo actualizando");

        	this.vehiculoParqueo.setFechaMod(new Date());
        	this.vehiculoParqueo.setUsuarioMod(nombre);
        	this.vehiculoParqueo.setFlagEstado("AC");
        	this.vehiculoParqueo.setFechaIngreso(new Date());
        	this.entityManager.merge(this.vehiculoParqueo);
            return "view?faces-redirect=true&id=" + this.vehiculoParqueo.getIdVehiculoParqueo();
         }
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   public String delete()
   {
      this.conversation.end();

      try
      {
         /*VehiculoParqueo deletableEntity = findById(getId());
         Compania compania = deletableEntity.getCompania();
         compania.getVehiculoParqueos().remove(deletableEntity);
         deletableEntity.setCompania(null);
         this.entityManager.merge(compania);
         Parqueo parqueo = deletableEntity.getParqueo();
         parqueo.getVehiculoParqueos().remove(deletableEntity);
         deletableEntity.setParqueo(null);
         this.entityManager.merge(parqueo);
         Vehiculo vehiculo = deletableEntity.getVehiculo();
         vehiculo.getVehiculoParqueos().remove(deletableEntity);
         deletableEntity.setVehiculo(null);
         this.entityManager.merge(vehiculo);
         this.entityManager.remove(deletableEntity);
         this.entityManager.flush();
         return "search?faces-redirect=true";*/
    	  String nombre = request.getUserPrincipal().getName();
    	  VehiculoParqueo deletableEntity = findById(getId());
    	  deletableEntity.setFlagEstado("IN");
    	  deletableEntity.setUsuarioBorrado(nombre);
    	  deletableEntity.setFechaBorrado(new Date());
    	  this.entityManager.flush();
          return "search?faces-redirect=true";
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   /*
    * Support searching VehiculoParqueo entities with pagination
    */

   private int page;
   private long count;
   private List<VehiculoParqueo> pageItems;

   private VehiculoParqueo example = new VehiculoParqueo();

   public int getPage()
   {
      return this.page;
   }

   public void setPage(int page)
   {
      this.page = page;
   }

   public int getPageSize()
   {
      return 10;
   }

   public VehiculoParqueo getExample()
   {
      return this.example;
   }

   public void setExample(VehiculoParqueo example)
   {
      this.example = example;
   }

   public String search()
   {
      this.page = 0;
      return null;
   }

   public void paginate()
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

      // Populate this.count

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<VehiculoParqueo> root = countCriteria.from(VehiculoParqueo.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<VehiculoParqueo> criteria = builder.createQuery(VehiculoParqueo.class);
      root = criteria.from(VehiculoParqueo.class);
      TypedQuery<VehiculoParqueo> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<VehiculoParqueo> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idVehiculoParqueo = this.example.getIdVehiculoParqueo();
      if (idVehiculoParqueo != 0)
      {
         predicatesList.add(builder.equal(root.get("idVehiculoParqueo"), idVehiculoParqueo));
      }
      Compania compania = this.example.getCompania();
      if (compania != null)
      {
         predicatesList.add(builder.equal(root.get("compania"), compania));
      }
      Parqueo parqueo = this.example.getParqueo();
      if (parqueo != null)
      {
         predicatesList.add(builder.equal(root.get("parqueo"), parqueo));
      }
      Vehiculo vehiculo = this.example.getVehiculo();
      if (vehiculo != null)
      {
         predicatesList.add(builder.equal(root.get("vehiculo"), vehiculo));
      }
      String flagEstado = this.example.getFlagEstado();
      if (flagEstado != null && !"".equals(flagEstado))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("flagEstado")), '%' + flagEstado.toLowerCase() + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<VehiculoParqueo> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back VehiculoParqueo entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<VehiculoParqueo> getAll()
   {

      CriteriaQuery<VehiculoParqueo> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(VehiculoParqueo.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(VehiculoParqueo.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final VehiculoParqueoBean ejbProxy = this.sessionContext.getBusinessObject(VehiculoParqueoBean.class);

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context,
               UIComponent component, String value)
         {

            return ejbProxy.findById(Integer.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context,
               UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((VehiculoParqueo) value).getIdVehiculoParqueo());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private VehiculoParqueo add = new VehiculoParqueo();

   public VehiculoParqueo getAdd()
   {
      return this.add;
   }

   public VehiculoParqueo getAdded()
   {
      VehiculoParqueo added = this.add;
      this.add = new VehiculoParqueo();
      return added;
   }
}
