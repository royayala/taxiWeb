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

import com.sinapsistech.taxiWeb.model.Vehiculo;
import com.sinapsistech.taxiWeb.model.Carrera;
import com.sinapsistech.taxiWeb.model.Compania;
import com.sinapsistech.taxiWeb.model.DetallaPersonaServicio;
import com.sinapsistech.taxiWeb.model.TipoVehiculo;
import com.sinapsistech.taxiWeb.model.Transaccion;
import com.sinapsistech.taxiWeb.model.VehiculoAfiliacion;
import com.sinapsistech.taxiWeb.model.VehiculoParqueo;
import com.sinapsistech.taxiWeb.model.VehiculoPersona;

import java.util.Iterator;

/**
 * Backing bean for Vehiculo entities.
 * <p/>
 * This class provides CRUD functionality for all Vehiculo entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class VehiculoBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Vehiculo entities
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

   private Vehiculo vehiculo;

   public Vehiculo getVehiculo()
   {
      return this.vehiculo;
   }

   public void setVehiculo(Vehiculo vehiculo)
   {
      this.vehiculo = vehiculo;
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
         this.vehiculo = this.example;
      }
      else
      {
         this.vehiculo = findById(getId());
      }
   }

   public Vehiculo findById(Integer id)
   {

      return this.entityManager.find(Vehiculo.class, id);
   }

   /*
    * Support updating and deleting Vehiculo entities
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
        	System.out.println("Entro por usuario nuevo");
        	this.vehiculo.setFechaReg(new Date());
        	this.vehiculo.setUsuarioReg(nombre);
            this.entityManager.persist(this.vehiculo);
            return "search?faces-redirect=true";
         }
         else
         {
        	 System.out.println("Entro por usuario actualizando");
        	 this.vehiculo.setFechaMod(new Date());
        	 this.vehiculo.setUsuarioMod(nombre);
        	 this.entityManager.merge(this.vehiculo);
            return "view?faces-redirect=true&id=" + this.vehiculo.getIdVehiculo();
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
         Vehiculo deletableEntity = findById(getId());
         Compania compania = deletableEntity.getCompania();
         compania.getVehiculos().remove(deletableEntity);
         deletableEntity.setCompania(null);
         this.entityManager.merge(compania);
         TipoVehiculo tipoVehiculo = deletableEntity.getTipoVehiculo();
         tipoVehiculo.getVehiculos().remove(deletableEntity);
         deletableEntity.setTipoVehiculo(null);
         this.entityManager.merge(tipoVehiculo);
         Iterator<VehiculoPersona> iterVehiculoPersonas = deletableEntity.getVehiculoPersonas().iterator();
         for (; iterVehiculoPersonas.hasNext();)
         {
            VehiculoPersona nextInVehiculoPersonas = iterVehiculoPersonas.next();
            nextInVehiculoPersonas.setVehiculo(null);
            iterVehiculoPersonas.remove();
            this.entityManager.merge(nextInVehiculoPersonas);
         }
         Iterator<Carrera> iterCarreras = deletableEntity.getCarreras().iterator();
         for (; iterCarreras.hasNext();)
         {
            Carrera nextInCarreras = iterCarreras.next();
            nextInCarreras.setVehiculo(null);
            iterCarreras.remove();
            this.entityManager.merge(nextInCarreras);
         }
         Iterator<VehiculoParqueo> iterVehiculoParqueos = deletableEntity.getVehiculoParqueos().iterator();
         for (; iterVehiculoParqueos.hasNext();)
         {
            VehiculoParqueo nextInVehiculoParqueos = iterVehiculoParqueos.next();
            nextInVehiculoParqueos.setVehiculo(null);
            iterVehiculoParqueos.remove();
            this.entityManager.merge(nextInVehiculoParqueos);
         }
         Iterator<DetallaPersonaServicio> iterDetallaPersonaServicios = deletableEntity.getDetallaPersonaServicios().iterator();
         for (; iterDetallaPersonaServicios.hasNext();)
         {
            DetallaPersonaServicio nextInDetallaPersonaServicios = iterDetallaPersonaServicios.next();
            nextInDetallaPersonaServicios.setVehiculo(null);
            iterDetallaPersonaServicios.remove();
            this.entityManager.merge(nextInDetallaPersonaServicios);
         }
         Iterator<Transaccion> iterTransaccions = deletableEntity.getTransaccions().iterator();
         for (; iterTransaccions.hasNext();)
         {
            Transaccion nextInTransaccions = iterTransaccions.next();
            nextInTransaccions.setVehiculo(null);
            iterTransaccions.remove();
            this.entityManager.merge(nextInTransaccions);
         }
         Iterator<VehiculoAfiliacion> iterVehiculoAfiliacions = deletableEntity.getVehiculoAfiliacions().iterator();
         for (; iterVehiculoAfiliacions.hasNext();)
         {
            VehiculoAfiliacion nextInVehiculoAfiliacions = iterVehiculoAfiliacions.next();
            nextInVehiculoAfiliacions.setVehiculo(null);
            iterVehiculoAfiliacions.remove();
            this.entityManager.merge(nextInVehiculoAfiliacions);
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
    * Support searching Vehiculo entities with pagination
    */

   private int page;
   private long count;
   private List<Vehiculo> pageItems;

   private Vehiculo example = new Vehiculo();

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

   public Vehiculo getExample()
   {
      return this.example;
   }

   public void setExample(Vehiculo example)
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
      Root<Vehiculo> root = countCriteria.from(Vehiculo.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Vehiculo> criteria = builder.createQuery(Vehiculo.class);
      root = criteria.from(Vehiculo.class);
      TypedQuery<Vehiculo> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Vehiculo> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idVehiculo = this.example.getIdVehiculo();
      if (idVehiculo != 0)
      {
         predicatesList.add(builder.equal(root.get("idVehiculo"), idVehiculo));
      }
      Compania compania = this.example.getCompania();
      if (compania != null)
      {
         predicatesList.add(builder.equal(root.get("compania"), compania));
      }
      TipoVehiculo tipoVehiculo = this.example.getTipoVehiculo();
      if (tipoVehiculo != null)
      {
         predicatesList.add(builder.equal(root.get("tipoVehiculo"), tipoVehiculo));
      }
      String placa = this.example.getPlaca();
      if (placa != null && !"".equals(placa))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("placa")), '%' + placa.toLowerCase() + '%'));
      }
      String modelo = this.example.getModelo();
      if (modelo != null && !"".equals(modelo))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("modelo")), '%' + modelo.toLowerCase() + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Vehiculo> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Vehiculo entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Vehiculo> getAll()
   {

      CriteriaQuery<Vehiculo> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Vehiculo.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Vehiculo.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final VehiculoBean ejbProxy = this.sessionContext.getBusinessObject(VehiculoBean.class);

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

            return String.valueOf(((Vehiculo) value).getIdVehiculo());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Vehiculo add = new Vehiculo();

   public Vehiculo getAdd()
   {
      return this.add;
   }

   public Vehiculo getAdded()
   {
      Vehiculo added = this.add;
      this.add = new Vehiculo();
      return added;
   }
}
