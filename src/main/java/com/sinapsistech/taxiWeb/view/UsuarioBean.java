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

import com.sinapsistech.taxiWeb.model.Usuario;
import com.sinapsistech.taxiWeb.model.Compania;

/**
 * Backing bean for Usuario entities.
 * <p/>
 * This class provides CRUD functionality for all Usuario entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class UsuarioBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Usuario entities
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

   private Usuario usuario;

   public Usuario getUsuario()
   {
      return this.usuario;
   }

   public void setUsuario(Usuario usuario)
   {
      this.usuario = usuario;
   }

   @Inject
   private Conversation conversation;

   @PersistenceContext(unitName = "taxiWeb-persistence-unit", type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   public String create()
   {

	      System.out.println("Entro por Create()");
	   this.conversation.begin();
      this.conversation.setTimeout(1800000L);
      return "create?faces-redirect=true";

   }

   public void retrieve()
   {
	      System.out.println("Entro por Retrieve()");
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
         this.usuario = this.example;
      }
      else
      {
         this.usuario = findById(getId());
      }
   }

   public Usuario findById(Integer id)
   {
	      System.out.println("Entro por FindBy()");
      return this.entityManager.find(Usuario.class, id);
   }

   /*
    * Support updating and deleting Usuario entities
    */

   public String update()
   {
	      System.out.println("Entro por Udpdate()");
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.usuario);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.usuario);
            return "view?faces-redirect=true&id=" + this.usuario.getIdUsuario();
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
	      System.out.println("Entro por Delete()");
      this.conversation.end();

      try
      {
         Usuario deletableEntity = findById(getId());
         Compania compania = deletableEntity.getCompania();
         compania.getUsuarios().remove(deletableEntity);
         deletableEntity.setCompania(null);
         this.entityManager.merge(compania);
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
    * Support searching Usuario entities with pagination
    */

   private int page;
   private long count;
   private List<Usuario> pageItems;

   private Usuario example = new Usuario();

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

   public Usuario getExample()
   {
      return this.example;
   }

   public void setExample(Usuario example)
   {
      this.example = example;
   }

   public String search()
   {
	      System.out.println("Entro por search()");
      this.page = 0;
      return null;
   }

   public void paginate()
   {
	      System.out.println("Entro por paginate()");

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

      // Populate this.count

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<Usuario> root = countCriteria.from(Usuario.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Usuario> criteria = builder.createQuery(Usuario.class);
      root = criteria.from(Usuario.class);
      TypedQuery<Usuario> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Usuario> root)
   {
	      System.out.println("Entro por getSearchPredicate()");

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      int idUsuario = this.example.getIdUsuario();
      if (idUsuario != 0)
      {
         predicatesList.add(builder.equal(root.get("idUsuario"), idUsuario));
      }
      Compania compania = this.example.getCompania();
      if (compania != null)
      {
         predicatesList.add(builder.equal(root.get("compania"), compania));
      }
      String nombreCompleto = this.example.getNombreCompleto();
      if (nombreCompleto != null && !"".equals(nombreCompleto))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("nombreCompleto")), '%' + nombreCompleto.toLowerCase() + '%'));
      }
      String usuarioLogin = this.example.getUsuarioLogin();
      if (usuarioLogin != null && !"".equals(usuarioLogin))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("usuarioLogin")), '%' + usuarioLogin.toLowerCase() + '%'));
      }
      String usuarioPassword = this.example.getUsuarioPassword();
      if (usuarioPassword != null && !"".equals(usuarioPassword))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("usuarioPassword")), '%' + usuarioPassword.toLowerCase() + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Usuario> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Usuario entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Usuario> getAll()
   {
	      System.out.println("Entro por getAll()");
      CriteriaQuery<Usuario> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Usuario.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Usuario.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {
	      System.out.println("Entro por getConverter()");
      final UsuarioBean ejbProxy = this.sessionContext.getBusinessObject(UsuarioBean.class);

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

            return String.valueOf(((Usuario) value).getIdUsuario());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Usuario add = new Usuario();

   public Usuario getAdd()
   {
	      System.out.println("Entro por getAdd()");
      return this.add;
   }

   public Usuario getAdded()
   {
	      System.out.println("Entro por getAdded()");
      Usuario added = this.add;
      this.add = new Usuario();
      return added;
   }
}
