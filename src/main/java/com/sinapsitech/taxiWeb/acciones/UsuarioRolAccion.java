package com.sinapsitech.taxiWeb.acciones;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
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

import org.primefaces.model.DualListModel;

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
public class UsuarioRolAccion implements Serializable
{

   private static final long serialVersionUID = 1L;
   FacesContext context = FacesContext.getCurrentInstance();
   ExternalContext externalContext = context.getExternalContext();
   HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

   /*
    * Support creating and retrieving Usuario entities
    */
   
   private DualListModel<String> roles;
   List<String> rolesSource = new ArrayList<String>();
   List<String> rolesTarget = new ArrayList<String>();
   
   private Integer id;
   
   @PostConstruct
   public void init() {
       //Cities
     
        
       rolesSource.add("USUARIO1");
       rolesSource.add("USUARIO2");
       rolesSource.add("ADMIN");
       rolesSource.add("SUPER");
        
       setRoles(new DualListModel<String>(rolesSource, rolesTarget));
        
   }
   

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
      final UsuarioRolAccion ejbProxy = this.sessionContext.getBusinessObject(UsuarioRolAccion.class);

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


public DualListModel<String> getRoles() {
	return roles;
}


public void setRoles(DualListModel<String> roles) {
	this.roles = roles;
}




public String grabarRoles()
{
	      System.out.println("Entro por GrabarRoles()");
   this.conversation.end();

   try
   {
	   //desactivo todos los roles asignados a ese usuario
	   
	   System.out.print("Actualizando Roles asignados anteriormente del usuario....: "+this.id);
	   
	   String consultaActualizacion = "update usuario_rol set flag_estado='IN' where id_usuario="+this.id;
	   this.entityManager.createNativeQuery(consultaActualizacion).executeUpdate();
	   
	   //grabar los nuevos roles
	   
	   System.out.println("Agarrando el contexto.");
       String nombreUsuarioGraba = request.getUserPrincipal().getName();
	   
	   System.out.print("Roles asignados....:");
	   for(Iterator it = roles.getTarget().iterator(); it.hasNext();) {
		   String rolAsignado = (String) it.next();
	       
		   System.out.print("\t" + rolAsignado);
		   
		// lo grabamos por consulta nativa.
		String consultaNuevoRoles = "insert into usuario_rol(id_usuario, rol_asignado, flag_estado,fecha_reg, usuario_reg) values (?,?,?,?,?)";
		this.entityManager.createNativeQuery(consultaNuevoRoles).setParameter(1, this.id).setParameter(2, rolAsignado).setParameter(3, "AC").setParameter(4, new Date()).setParameter(5, nombreUsuarioGraba).executeUpdate();
		
	   }
	   this.entityManager.flush();
	   
	   FacesContext context = FacesContext.getCurrentInstance();
       context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Roles asignados al usuario", null));
       
       return "search?faces-redirect=true";
 	  
   }
   catch (Exception e)
   {
	   System.out.println("Error al grabar los roles asignados"+e.getMessage());
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
      return null;
   }
}

}