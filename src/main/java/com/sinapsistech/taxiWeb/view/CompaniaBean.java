package com.sinapsistech.taxiWeb.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
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

import com.sinapsistech.taxiWeb.model.Compania;
import com.sinapsistech.taxiWeb.model.Carrera;
import com.sinapsistech.taxiWeb.model.DetallaPersonaServicio;
import com.sinapsistech.taxiWeb.model.Parqueo;
import com.sinapsistech.taxiWeb.model.Persona;
import com.sinapsistech.taxiWeb.model.PersonaServicio;
import com.sinapsistech.taxiWeb.model.Servicio;
import com.sinapsistech.taxiWeb.model.Tarifa;
import com.sinapsistech.taxiWeb.model.TipoTransaccion;
import com.sinapsistech.taxiWeb.model.Transaccion;
import com.sinapsistech.taxiWeb.model.Usuario;
import com.sinapsistech.taxiWeb.model.Vehiculo;
import com.sinapsistech.taxiWeb.model.VehiculoAfiliacion;
import com.sinapsistech.taxiWeb.model.VehiculoParqueo;
import com.sinapsistech.taxiWeb.model.VehiculoPersona;
import java.util.Iterator;

/**
 * Backing bean for Compania entities.
 * <p/>
 * This class provides CRUD functionality for all Compania entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class CompaniaBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Compania entities
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

   private Compania compania;

   public Compania getCompania()
   {
      return this.compania;
   }

   public void setCompania(Compania compania)
   {
      this.compania = compania;
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
         this.compania = this.example;
      }
      else
      {
         this.compania = findById(getId());
      }
   }

   public Compania findById(Integer id)
   {

      return this.entityManager.find(Compania.class, id);
   }

   /*
    * Support updating and deleting Compania entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.compania);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.compania);
            return "view?faces-redirect=true&id=" + this.compania.getIdCia();
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
         Compania deletableEntity = findById(getId());
         Iterator<DetallaPersonaServicio> iterDetallaPersonaServicios = deletableEntity.getDetallaPersonaServicios().iterator();
         for (; iterDetallaPersonaServicios.hasNext();)
         {
            DetallaPersonaServicio nextInDetallaPersonaServicios = iterDetallaPersonaServicios.next();
            nextInDetallaPersonaServicios.setCompania(null);
            iterDetallaPersonaServicios.remove();
            this.entityManager.merge(nextInDetallaPersonaServicios);
         }
         Iterator<Parqueo> iterParqueos = deletableEntity.getParqueos().iterator();
         for (; iterParqueos.hasNext();)
         {
            Parqueo nextInParqueos = iterParqueos.next();
            nextInParqueos.setCompania(null);
            iterParqueos.remove();
            this.entityManager.merge(nextInParqueos);
         }
         Iterator<VehiculoParqueo> iterVehiculoParqueos = deletableEntity.getVehiculoParqueos().iterator();
         for (; iterVehiculoParqueos.hasNext();)
         {
            VehiculoParqueo nextInVehiculoParqueos = iterVehiculoParqueos.next();
            nextInVehiculoParqueos.setCompania(null);
            iterVehiculoParqueos.remove();
            this.entityManager.merge(nextInVehiculoParqueos);
         }
         Iterator<VehiculoPersona> iterVehiculoPersonas = deletableEntity.getVehiculoPersonas().iterator();
         for (; iterVehiculoPersonas.hasNext();)
         {
            VehiculoPersona nextInVehiculoPersonas = iterVehiculoPersonas.next();
            nextInVehiculoPersonas.setCompania(null);
            iterVehiculoPersonas.remove();
            this.entityManager.merge(nextInVehiculoPersonas);
         }
         Iterator<PersonaServicio> iterPersonaServicios = deletableEntity.getPersonaServicios().iterator();
         for (; iterPersonaServicios.hasNext();)
         {
            PersonaServicio nextInPersonaServicios = iterPersonaServicios.next();
            nextInPersonaServicios.setCompania(null);
            iterPersonaServicios.remove();
            this.entityManager.merge(nextInPersonaServicios);
         }
         Iterator<Tarifa> iterTarifas = deletableEntity.getTarifas().iterator();
         for (; iterTarifas.hasNext();)
         {
            Tarifa nextInTarifas = iterTarifas.next();
            nextInTarifas.setCompania(null);
            iterTarifas.remove();
            this.entityManager.merge(nextInTarifas);
         }
         Iterator<Persona> iterPersonas = deletableEntity.getPersonas().iterator();
         for (; iterPersonas.hasNext();)
         {
            Persona nextInPersonas = iterPersonas.next();
            nextInPersonas.setCompania(null);
            iterPersonas.remove();
            this.entityManager.merge(nextInPersonas);
         }
         Iterator<Vehiculo> iterVehiculos = deletableEntity.getVehiculos().iterator();
         for (; iterVehiculos.hasNext();)
         {
            Vehiculo nextInVehiculos = iterVehiculos.next();
            nextInVehiculos.setCompania(null);
            iterVehiculos.remove();
            this.entityManager.merge(nextInVehiculos);
         }
         Iterator<VehiculoAfiliacion> iterVehiculoAfiliacions = deletableEntity.getVehiculoAfiliacions().iterator();
         for (; iterVehiculoAfiliacions.hasNext();)
         {
            VehiculoAfiliacion nextInVehiculoAfiliacions = iterVehiculoAfiliacions.next();
            nextInVehiculoAfiliacions.setCompania(null);
            iterVehiculoAfiliacions.remove();
            this.entityManager.merge(nextInVehiculoAfiliacions);
         }
         Iterator<Carrera> iterCarreras = deletableEntity.getCarreras().iterator();
         for (; iterCarreras.hasNext();)
         {
            Carrera nextInCarreras = iterCarreras.next();
            nextInCarreras.setCompania(null);
            iterCarreras.remove();
            this.entityManager.merge(nextInCarreras);
         }
         Iterator<Transaccion> iterTransaccions = deletableEntity.getTransaccions().iterator();
         for (; iterTransaccions.hasNext();)
         {
            Transaccion nextInTransaccions = iterTransaccions.next();
            nextInTransaccions.setCompania(null);
            iterTransaccions.remove();
            this.entityManager.merge(nextInTransaccions);
         }
         Iterator<Usuario> iterUsuarios = deletableEntity.getUsuarios().iterator();
         for (; iterUsuarios.hasNext();)
         {
            Usuario nextInUsuarios = iterUsuarios.next();
            nextInUsuarios.setCompania(null);
            iterUsuarios.remove();
            this.entityManager.merge(nextInUsuarios);
         }
         Iterator<Servicio> iterServicios = deletableEntity.getServicios().iterator();
         for (; iterServicios.hasNext();)
         {
            Servicio nextInServicios = iterServicios.next();
            nextInServicios.setCompania(null);
            iterServicios.remove();
            this.entityManager.merge(nextInServicios);
         }
         Iterator<TipoTransaccion> iterTipoTransaccions = deletableEntity.getTipoTransaccions().iterator();
         for (; iterTipoTransaccions.hasNext();)
         {
            TipoTransaccion nextInTipoTransaccions = iterTipoTransaccions.next();
            nextInTipoTransaccions.setCompania(null);
            iterTipoTransaccions.remove();
            this.entityManager.merge(nextInTipoTransaccions);
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
    * Support searching Compania entities with pagination
    */

   private int page;
   private long count;
   private List<Compania> pageItems;

   private Compania example = new Compania();

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

   public Compania getExample()
   {
      return this.example;
   }

   public void setExample(Compania example)
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
      Root<Compania> root = countCriteria.from(Compania.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Compania> criteria = builder.createQuery(Compania.class);
      root = criteria.from(Compania.class);
      TypedQuery<Compania> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Compania> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idCia = this.example.getIdCia();
      if (idCia != 0)
      {
         predicatesList.add(builder.equal(root.get("idCia"), idCia));
      }
      String nombreCorto = this.example.getNombreCorto();
      if (nombreCorto != null && !"".equals(nombreCorto))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("nombreCorto")), '%' + nombreCorto.toLowerCase() + '%'));
      }
      String nombreLargo = this.example.getNombreLargo();
      if (nombreLargo != null && !"".equals(nombreLargo))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("nombreLargo")), '%' + nombreLargo.toLowerCase() + '%'));
      }
      String flagEstado = this.example.getFlagEstado();
      if (flagEstado != null && !"".equals(flagEstado))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("flagEstado")), '%' + flagEstado.toLowerCase() + '%'));
      }
      String usuarioReg = this.example.getUsuarioReg();
      if (usuarioReg != null && !"".equals(usuarioReg))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("usuarioReg")), '%' + usuarioReg.toLowerCase() + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Compania> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Compania entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Compania> getAll()
   {

      CriteriaQuery<Compania> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Compania.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Compania.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final CompaniaBean ejbProxy = this.sessionContext.getBusinessObject(CompaniaBean.class);

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

            return String.valueOf(((Compania) value).getIdCia());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Compania add = new Compania();

   public Compania getAdd()
   {
      return this.add;
   }

   public Compania getAdded()
   {
      Compania added = this.add;
      this.add = new Compania();
      return added;
   }
}
