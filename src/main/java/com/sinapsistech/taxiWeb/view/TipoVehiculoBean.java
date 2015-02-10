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

import com.sinapsistech.taxiWeb.model.TipoVehiculo;
import com.sinapsistech.taxiWeb.model.Vehiculo;
import java.util.Iterator;

/**
 * Backing bean for TipoVehiculo entities.
 * <p/>
 * This class provides CRUD functionality for all TipoVehiculo entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class TipoVehiculoBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving TipoVehiculo entities
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

   private TipoVehiculo tipoVehiculo;

   public TipoVehiculo getTipoVehiculo()
   {
      return this.tipoVehiculo;
   }

   public void setTipoVehiculo(TipoVehiculo tipoVehiculo)
   {
      this.tipoVehiculo = tipoVehiculo;
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
         this.tipoVehiculo = this.example;
      }
      else
      {
         this.tipoVehiculo = findById(getId());
      }
   }

   public TipoVehiculo findById(Integer id)
   {

      return this.entityManager.find(TipoVehiculo.class, id);
   }

   /*
    * Support updating and deleting TipoVehiculo entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.tipoVehiculo);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.tipoVehiculo);
            return "view?faces-redirect=true&id=" + this.tipoVehiculo.getIdTipoVehiculo();
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
         TipoVehiculo deletableEntity = findById(getId());
         Iterator<Vehiculo> iterVehiculos = deletableEntity.getVehiculos().iterator();
         for (; iterVehiculos.hasNext();)
         {
            Vehiculo nextInVehiculos = iterVehiculos.next();
            nextInVehiculos.setTipoVehiculo(null);
            iterVehiculos.remove();
            this.entityManager.merge(nextInVehiculos);
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
    * Support searching TipoVehiculo entities with pagination
    */

   private int page;
   private long count;
   private List<TipoVehiculo> pageItems;

   private TipoVehiculo example = new TipoVehiculo();

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

   public TipoVehiculo getExample()
   {
      return this.example;
   }

   public void setExample(TipoVehiculo example)
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
      Root<TipoVehiculo> root = countCriteria.from(TipoVehiculo.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<TipoVehiculo> criteria = builder.createQuery(TipoVehiculo.class);
      root = criteria.from(TipoVehiculo.class);
      TypedQuery<TipoVehiculo> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<TipoVehiculo> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idTipoVehiculo = this.example.getIdTipoVehiculo();
      if (idTipoVehiculo != 0)
      {
         predicatesList.add(builder.equal(root.get("idTipoVehiculo"), idTipoVehiculo));
      }
      String nombre = this.example.getNombre();
      if (nombre != null && !"".equals(nombre))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("nombre")), '%' + nombre.toLowerCase() + '%'));
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
      String usuarioMod = this.example.getUsuarioMod();
      if (usuarioMod != null && !"".equals(usuarioMod))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("usuarioMod")), '%' + usuarioMod.toLowerCase() + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<TipoVehiculo> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back TipoVehiculo entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<TipoVehiculo> getAll()
   {

      CriteriaQuery<TipoVehiculo> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(TipoVehiculo.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(TipoVehiculo.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final TipoVehiculoBean ejbProxy = this.sessionContext.getBusinessObject(TipoVehiculoBean.class);

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

            return String.valueOf(((TipoVehiculo) value).getIdTipoVehiculo());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private TipoVehiculo add = new TipoVehiculo();

   public TipoVehiculo getAdd()
   {
      return this.add;
   }

   public TipoVehiculo getAdded()
   {
      TipoVehiculo added = this.add;
      this.add = new TipoVehiculo();
      return added;
   }
}
