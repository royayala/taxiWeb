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

import com.sinapsistech.taxiWeb.model.TipoTransaccion;
import com.sinapsistech.taxiWeb.model.Compania;
import com.sinapsistech.taxiWeb.model.Transaccion;
import java.util.Iterator;

/**
 * Backing bean for TipoTransaccion entities.
 * <p/>
 * This class provides CRUD functionality for all TipoTransaccion entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class TipoTransaccionBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving TipoTransaccion entities
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

   private TipoTransaccion tipoTransaccion;

   public TipoTransaccion getTipoTransaccion()
   {
      return this.tipoTransaccion;
   }

   public void setTipoTransaccion(TipoTransaccion tipoTransaccion)
   {
      this.tipoTransaccion = tipoTransaccion;
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
         this.tipoTransaccion = this.example;
      }
      else
      {
         this.tipoTransaccion = findById(getId());
      }
   }

   public TipoTransaccion findById(Integer id)
   {

      return this.entityManager.find(TipoTransaccion.class, id);
   }

   /*
    * Support updating and deleting TipoTransaccion entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.tipoTransaccion);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.tipoTransaccion);
            return "view?faces-redirect=true&id=" + this.tipoTransaccion.getIdTipoTransaccion();
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
         TipoTransaccion deletableEntity = findById(getId());
         Compania compania = deletableEntity.getCompania();
         compania.getTipoTransaccions().remove(deletableEntity);
         deletableEntity.setCompania(null);
         this.entityManager.merge(compania);
         Iterator<Transaccion> iterTransaccions = deletableEntity.getTransaccions().iterator();
         for (; iterTransaccions.hasNext();)
         {
            Transaccion nextInTransaccions = iterTransaccions.next();
            nextInTransaccions.setTipoTransaccion(null);
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
    * Support searching TipoTransaccion entities with pagination
    */

   private int page;
   private long count;
   private List<TipoTransaccion> pageItems;

   private TipoTransaccion example = new TipoTransaccion();

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

   public TipoTransaccion getExample()
   {
      return this.example;
   }

   public void setExample(TipoTransaccion example)
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
      Root<TipoTransaccion> root = countCriteria.from(TipoTransaccion.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<TipoTransaccion> criteria = builder.createQuery(TipoTransaccion.class);
      root = criteria.from(TipoTransaccion.class);
      TypedQuery<TipoTransaccion> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<TipoTransaccion> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idTipoTransaccion = this.example.getIdTipoTransaccion();
      if (idTipoTransaccion != 0)
      {
         predicatesList.add(builder.equal(root.get("idTipoTransaccion"), idTipoTransaccion));
      }
      Compania compania = this.example.getCompania();
      if (compania != null)
      {
         predicatesList.add(builder.equal(root.get("compania"), compania));
      }
      String nombre = this.example.getNombre();
      if (nombre != null && !"".equals(nombre))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("nombre")), '%' + nombre.toLowerCase() + '%'));
      }
      String descripcion = this.example.getDescripcion();
      if (descripcion != null && !"".equals(descripcion))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("descripcion")), '%' + descripcion.toLowerCase() + '%'));
      }
      String tipo = this.example.getTipo();
      if (tipo != null && !"".equals(tipo))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("tipo")), '%' + tipo.toLowerCase() + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<TipoTransaccion> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back TipoTransaccion entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<TipoTransaccion> getAll()
   {

      CriteriaQuery<TipoTransaccion> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(TipoTransaccion.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(TipoTransaccion.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final TipoTransaccionBean ejbProxy = this.sessionContext.getBusinessObject(TipoTransaccionBean.class);

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

            return String.valueOf(((TipoTransaccion) value).getIdTipoTransaccion());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private TipoTransaccion add = new TipoTransaccion();

   public TipoTransaccion getAdd()
   {
      return this.add;
   }

   public TipoTransaccion getAdded()
   {
      TipoTransaccion added = this.add;
      this.add = new TipoTransaccion();
      return added;
   }
}
