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

import com.sinapsistech.taxiWeb.model.VehiculoAfiliacion;
import com.sinapsistech.taxiWeb.model.Compania;
import com.sinapsistech.taxiWeb.model.Persona;
import com.sinapsistech.taxiWeb.model.Transaccion;
import com.sinapsistech.taxiWeb.model.Vehiculo;

import java.util.Iterator;

/**
 * Backing bean for VehiculoAfiliacion entities.
 * <p/>
 * This class provides CRUD functionality for all VehiculoAfiliacion entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class VehiculoAfiliacionBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving VehiculoAfiliacion entities
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

   private VehiculoAfiliacion vehiculoAfiliacion;

   public VehiculoAfiliacion getVehiculoAfiliacion()
   {
      return this.vehiculoAfiliacion;
   }

   public void setVehiculoAfiliacion(VehiculoAfiliacion vehiculoAfiliacion)
   {
      this.vehiculoAfiliacion = vehiculoAfiliacion;
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
         this.vehiculoAfiliacion = this.example;
      }
      else
      {
         this.vehiculoAfiliacion = findById(getId());
      }
   }

   public VehiculoAfiliacion findById(Integer id)
   {

      return this.entityManager.find(VehiculoAfiliacion.class, id);
   }

   /*
    * Support updating and deleting VehiculoAfiliacion entities
    */

   public String update()
   {
	   FacesContext context = FacesContext.getCurrentInstance();
       ExternalContext externalContext = context.getExternalContext();
       HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
       System.out.println("Agarrando el contexto.");
       String nombre = request.getUserPrincipal().getName();
      
	   this.conversation.end();

      try
      {
         if (this.id == null)
         {
        	System.out.println("Entro por vehiculoAfiliacion nuevo");
        	
        	this.vehiculoAfiliacion.setFechaReg(new Date());
        	this.vehiculoAfiliacion.setUsuarioReg(nombre);
            this.entityManager.persist(this.vehiculoAfiliacion);
            return "search?faces-redirect=true";
         }
         else
         {
        	System.out.println("Entro por vehiculoAfiliacion actualizar");
        	
        	this.vehiculoAfiliacion.setFechaMod(new Date());
        	this.vehiculoAfiliacion.setUsuarioMod(nombre);
            this.entityManager.merge(this.vehiculoAfiliacion);
            return "view?faces-redirect=true&id=" + this.vehiculoAfiliacion.getIdVehiculoAfiliacion();
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
         VehiculoAfiliacion deletableEntity = findById(getId());
         Compania compania = deletableEntity.getCompania();
         compania.getVehiculoAfiliacions().remove(deletableEntity);
         deletableEntity.setCompania(null);
         this.entityManager.merge(compania);
         Persona persona = deletableEntity.getPersona();
         persona.getVehiculoAfiliacions().remove(deletableEntity);
         deletableEntity.setPersona(null);
         this.entityManager.merge(persona);
         Vehiculo vehiculo = deletableEntity.getVehiculo();
         vehiculo.getVehiculoAfiliacions().remove(deletableEntity);
         deletableEntity.setVehiculo(null);
         this.entityManager.merge(vehiculo);
         Iterator<Transaccion> iterTransaccions = deletableEntity.getTransaccions().iterator();
         for (; iterTransaccions.hasNext();)
         {
            Transaccion nextInTransaccions = iterTransaccions.next();
            nextInTransaccions.setVehiculoAfiliacion(null);
            iterTransaccions.remove();
            this.entityManager.merge(nextInTransaccions);
         }
         
         this.entityManager.remove(deletableEntity);
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
    * Support searching VehiculoAfiliacion entities with pagination
    */

   private int page;
   private long count;
   private List<VehiculoAfiliacion> pageItems;

   private VehiculoAfiliacion example = new VehiculoAfiliacion();

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

   public VehiculoAfiliacion getExample()
   {
      return this.example;
   }

   public void setExample(VehiculoAfiliacion example)
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
      Root<VehiculoAfiliacion> root = countCriteria.from(VehiculoAfiliacion.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<VehiculoAfiliacion> criteria = builder.createQuery(VehiculoAfiliacion.class);
      root = criteria.from(VehiculoAfiliacion.class);
      TypedQuery<VehiculoAfiliacion> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<VehiculoAfiliacion> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idVehiculoAfiliacion = this.example.getIdVehiculoAfiliacion();
      if (idVehiculoAfiliacion != 0)
      {
         predicatesList.add(builder.equal(root.get("idVehiculoAfiliacion"), idVehiculoAfiliacion));
      }
      Compania compania = this.example.getCompania();
      if (compania != null)
      {
         predicatesList.add(builder.equal(root.get("compania"), compania));
      }
      Persona persona = this.example.getPersona();
      if (persona != null)
      {
         predicatesList.add(builder.equal(root.get("persona"), persona));
      }
      Vehiculo vehiculo = this.example.getVehiculo();
      if (vehiculo != null)
      {
         predicatesList.add(builder.equal(root.get("vehiculo"), vehiculo));
      }
      String descripcion = this.example.getDescripcion();
      if (descripcion != null && !"".equals(descripcion))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("descripcion")), '%' + descripcion.toLowerCase() + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<VehiculoAfiliacion> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back VehiculoAfiliacion entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<VehiculoAfiliacion> getAll()
   {

      CriteriaQuery<VehiculoAfiliacion> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(VehiculoAfiliacion.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(VehiculoAfiliacion.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final VehiculoAfiliacionBean ejbProxy = this.sessionContext.getBusinessObject(VehiculoAfiliacionBean.class);

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

            return String.valueOf(((VehiculoAfiliacion) value).getIdVehiculoAfiliacion());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private VehiculoAfiliacion add = new VehiculoAfiliacion();

   public VehiculoAfiliacion getAdd()
   {
      return this.add;
   }

   public VehiculoAfiliacion getAdded()
   {
      VehiculoAfiliacion added = this.add;
      this.add = new VehiculoAfiliacion();
      return added;
   }
}
