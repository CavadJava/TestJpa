package home;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hibernate.ejb.QueryHints;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class JPATest {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private EntityTransaction transaction;

    @Before
    public void init(){
        entityManagerFactory = Persistence.createEntityManagerFactory("jpa-1");
        entityManager = entityManagerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        transaction.begin();
    }

    @After
    public void destroy(){
        transaction.commit();
        entityManager.close();
        entityManagerFactory.close();
    }
/*
    //¿ÉÒÔÊ¹ÓÃ JPQL Íê³É UPDATE ºÍ DELETE ²Ù×÷.
    @Test
    public void testExecuteUpdate(){
        String jpql = "UPDATE Customer c SET c.lastName = ? WHERE c.id = ?";
        Query query = entityManager.createQuery(jpql).setParameter(1, "YYY").setParameter(2, 12);

        query.executeUpdate();
    }

    //Ê¹ÓÃ jpql ÄÚœšµÄº¯Êý
    @Test
    public void testJpqlFunction(){
        String jpql = "SELECT lower(c.email) FROM Customer c";

        List<String> emails = entityManager.createQuery(jpql).getResultList();
        System.out.println(emails);
    }

    @Test
    public void testSubQuery(){
        //²éÑ¯ËùÓÐ Customer µÄ lastName Îª YY µÄ Order
        String jpql = "SELECT o FROM Order o "
                + "WHERE o.customer = (SELECT c FROM Customer c WHERE c.lastName = ?)";

        Query query = entityManager.createQuery(jpql).setParameter(1, "YY");
        List<Order> orders = query.getResultList();
        System.out.println(orders.size());
    }

    *//**//**//**//**
     * JPQL µÄ¹ØÁª²éÑ¯Í¬ HQL µÄ¹ØÁª²éÑ¯.
     *//**//**//**//*
    @Test
    public void testLeftOuterJoinFetch(){
        String jpql = "FROM Customer c LEFT OUTER JOIN FETCH c.orders WHERE c.id = ?";

        Customer customer =
                (Customer) entityManager.createQuery(jpql).setParameter(1, 12).getSingleResult();
        System.out.println(customer.getLastName());
        System.out.println(customer.getOrders().size());

//		List<Object[]> result = entityManager.createQuery(jpql).setParameter(1, 12).getResultList();
//		System.out.println(result);
    }

    //²éÑ¯ order ÊýÁ¿ŽóÓÚ 2 µÄÄÇÐ© Customer
    @Test
    public void testGroupBy(){
        String jpql = "SELECT o.customer FROM Order o "
                + "GROUP BY o.customer "
                + "HAVING count(o.id) >= 2";
        List<Customer> customers = entityManager.createQuery(jpql).getResultList();

        System.out.println(customers);
    }

    @Test
    public void testOrderBy(){
        String jpql = "FROM Customer c WHERE c.age > ? ORDER BY c.age DESC";
        Query query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);

        //ÕŒÎ»·ûµÄË÷ÒýÊÇŽÓ 1 ¿ªÊŒ
        query.setParameter(1, 1);
        List<Customer> customers = query.getResultList();
        System.out.println(customers.size());
    }

    //Ê¹ÓÃ hibernate µÄ²éÑ¯»ºŽæ.
    @Test
    public void testQueryCache(){
        String jpql = "FROM Customer c WHERE c.age > ?";
        Query query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);

        //ÕŒÎ»·ûµÄË÷ÒýÊÇŽÓ 1 ¿ªÊŒ
        query.setParameter(1, 1);
        List<Customer> customers = query.getResultList();
        System.out.println(customers.size());

        query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);

        //ÕŒÎ»·ûµÄË÷ÒýÊÇŽÓ 1 ¿ªÊŒ
        query.setParameter(1, 1);
        customers = query.getResultList();
        System.out.println(customers.size());
    }

    //createNativeQuery ÊÊÓÃÓÚ±ŸµØ SQL
    @Test
    public void testNativeQuery(){
        String sql = "SELECT age FROM jpa_cutomers WHERE id = ?";
        Query query = entityManager.createNativeQuery(sql).setParameter(1, 3);

        Object result = query.getSingleResult();
        System.out.println(result);
    }

    //createNamedQuery ÊÊÓÃÓÚÔÚÊµÌåÀàÇ°Ê¹ÓÃ @NamedQuery ±êŒÇµÄ²éÑ¯ÓïŸä
    @Test
    public void testNamedQuery(){
        Query query = entityManager.createNamedQuery("testNamedQuery").setParameter(1, 3);
        Customer customer = (Customer) query.getSingleResult();

        System.out.println(customer);
    }

    //Ä¬ÈÏÇé¿öÏÂ, ÈôÖ»²éÑ¯²¿·ÖÊôÐÔ, Ôòœ«·µ»Ø Object[] ÀàÐÍµÄœá¹û. »òÕß Object[] ÀàÐÍµÄ List.
    //Ò²¿ÉÒÔÔÚÊµÌåÀàÖÐŽŽœš¶ÔÓŠµÄ¹¹ÔìÆ÷, È»ºóÔÙ JPQL ÓïŸäÖÐÀûÓÃ¶ÔÓŠµÄ¹¹ÔìÆ÷·µ»ØÊµÌåÀàµÄ¶ÔÏó.
    @Test
    public void testPartlyProperties(){
        String jpql = "SELECT new Customer(c.lastName, c.age) FROM Customer c WHERE c.id > ?";
        List result = entityManager.createQuery(jpql).setParameter(1, 1).getResultList();

        System.out.println(result);
    }

    @Test
    public void testHelloJPQL(){
        String jpql = "FROM Customer c WHERE c.age > ?";
        Query query = entityManager.createQuery(jpql);

        //ÕŒÎ»·ûµÄË÷ÒýÊÇŽÓ 1 ¿ªÊŒ
        query.setParameter(1, 1);
        List<Customer> customers = query.getResultList();
        System.out.println(customers.size());
    }

    @Test
    public void testSecondLevelCache(){
        Customer customer1 = entityManager.find(Customer.class, 1);

        transaction.commit();
        entityManager.close();

        entityManager = entityManagerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        transaction.begin();

        Customer customer2 = entityManager.find(Customer.class, 1);
    }

    //¶ÔÓÚ¹ØÁªµÄŒ¯ºÏ¶ÔÏó, Ä¬ÈÏÊ¹ÓÃÀÁŒÓÔØµÄ²ßÂÔ.
    //Ê¹ÓÃÎ¬»€¹ØÁª¹ØÏµµÄÒ»·œ»ñÈ¡, »¹ÊÇÊ¹ÓÃ²»Î¬»€¹ØÁª¹ØÏµµÄÒ»·œ»ñÈ¡, SQL ÓïŸäÏàÍ¬.
    @Test
    public void testManyToManyFind(){
//		Item item = entityManager.find(Item.class, 5);
//		System.out.println(item.getItemName());
//
//		System.out.println(item.getCategories().size());

        Category category = entityManager.find(Category.class, 3);
        System.out.println(category.getCategoryName());
        System.out.println(category.getItems().size());
    }

    //¶à¶ÔËùµÄ±£Žæ
    @Test
    public void testManyToManyPersist(){
        Item i1 = new Item();
        i1.setItemName("i-1");

        Item i2 = new Item();
        i2.setItemName("i-2");

        Category c1 = new Category();
        c1.setCategoryName("C-1");

        Category c2 = new Category();
        c2.setCategoryName("C-2");

        //ÉèÖÃ¹ØÁª¹ØÏµ
        i1.getCategories().add(c1);
        i1.getCategories().add(c2);

        i2.getCategories().add(c1);
        i2.getCategories().add(c2);

        c1.getItems().add(i1);
        c1.getItems().add(i2);

        c2.getItems().add(i1);
        c2.getItems().add(i2);

        //ÖŽÐÐ±£Žæ
        entityManager.persist(i1);
        entityManager.persist(i2);
        entityManager.persist(c1);
        entityManager.persist(c2);
    }

    //1. Ä¬ÈÏÇé¿öÏÂ, Èô»ñÈ¡²»Î¬»€¹ØÁª¹ØÏµµÄÒ»·œ, ÔòÒ²»áÍš¹ý×óÍâÁ¬œÓ»ñÈ¡Æä¹ØÁªµÄ¶ÔÏó.
    //¿ÉÒÔÍš¹ý @OneToOne µÄ fetch ÊôÐÔÀŽÐÞžÄŒÓÔØ²ßÂÔ. µ«ÒÀÈ»»áÔÙ·¢ËÍ SQL ÓïŸäÀŽ³õÊŒ»¯Æä¹ØÁªµÄ¶ÔÏó
    //ÕâËµÃ÷ÔÚ²»Î¬»€¹ØÁª¹ØÏµµÄÒ»·œ, ²»œšÒéÐÞžÄ fetch ÊôÐÔ.
    @Test
    public void testOneToOneFind2(){
        Manager mgr = entityManager.find(Manager.class, 1);
        System.out.println(mgr.getMgrName());

        System.out.println(mgr.getDept().getClass().getName());
    }

    //1.Ä¬ÈÏÇé¿öÏÂ, Èô»ñÈ¡Î¬»€¹ØÁª¹ØÏµµÄÒ»·œ, Ôò»áÍš¹ý×óÍâÁ¬œÓ»ñÈ¡Æä¹ØÁªµÄ¶ÔÏó.
    //µ«¿ÉÒÔÍš¹ý @OntToOne µÄ fetch ÊôÐÔÀŽÐÞžÄŒÓÔØ²ßÂÔ.
    @Test
    public void testOneToOneFind(){
        Department dept = entityManager.find(Department.class, 1);
        System.out.println(dept.getDeptName());
        System.out.println(dept.getMgr().getClass().getName());
    }

    //Ë«Ïò 1-1 µÄ¹ØÁª¹ØÏµ, œšÒéÏÈ±£Žæ²»Î¬»€¹ØÁª¹ØÏµµÄÒ»·œ, ŒŽÃ»ÓÐÍâŒüµÄÒ»·œ, ÕâÑù²»»á¶à³ö UPDATE ÓïŸä.
    @Test
    public void testOneToOnePersistence(){
        Manager mgr = new Manager();
        mgr.setMgrName("M-BB");

        Department dept = new Department();
        dept.setDeptName("D-BB");

        //ÉèÖÃ¹ØÁª¹ØÏµ
        mgr.setDept(dept);
        dept.setMgr(mgr);

        //ÖŽÐÐ±£Žæ²Ù×÷
        entityManager.persist(mgr);
        entityManager.persist(dept);
    }

    @Test
    public void testUpdate(){
        Customer customer = entityManager.find(Customer.class, 10);

        customer.getOrders().iterator().next().setOrderName("O-XXX-10");
    }

    //Ä¬ÈÏÇé¿öÏÂ, ÈôÉŸ³ý 1 µÄÒ»¶Ë, Ôò»áÏÈ°Ñ¹ØÁªµÄ n µÄÒ»¶ËµÄÍâŒüÖÃ¿Õ, È»ºóœøÐÐÉŸ³ý.
    //¿ÉÒÔÍš¹ý @OneToMany µÄ cascade ÊôÐÔÀŽÐÞžÄÄ¬ÈÏµÄÉŸ³ý²ßÂÔ.
    @Test
    public void testOneToManyRemove(){
        Customer customer = entityManager.find(Customer.class, 8);
        entityManager.remove(customer);
    }

    //Ä¬ÈÏ¶Ô¹ØÁªµÄ¶àµÄÒ»·œÊ¹ÓÃÀÁŒÓÔØµÄŒÓÔØ²ßÂÔ.
    //¿ÉÒÔÊ¹ÓÃ @OneToMany µÄ fetch ÊôÐÔÀŽÐÞžÄÄ¬ÈÏµÄŒÓÔØ²ßÂÔ
    @Test
    public void testOneToManyFind(){
        Customer customer = entityManager.find(Customer.class, 9);
        System.out.println(customer.getLastName());

        System.out.println(customer.getOrders().size());
    }

    //ÈôÊÇË«Ïò 1-n µÄ¹ØÁª¹ØÏµ, ÖŽÐÐ±£ŽæÊ±
    //ÈôÏÈ±£Žæ n µÄÒ»¶Ë, ÔÙ±£Žæ 1 µÄÒ»¶Ë, Ä¬ÈÏÇé¿öÏÂ, »á¶à³ö n Ìõ UPDATE ÓïŸä.
    //ÈôÏÈ±£Žæ 1 µÄÒ»¶Ë, Ôò»á¶à³ö n Ìõ UPDATE ÓïŸä
    //ÔÚœøÐÐË«Ïò 1-n ¹ØÁª¹ØÏµÊ±, œšÒéÊ¹ÓÃ n µÄÒ»·œÀŽÎ¬»€¹ØÁª¹ØÏµ, ¶ø 1 µÄÒ»·œ²»Î¬»€¹ØÁªÏµ, ÕâÑù»áÓÐÐ§µÄŒõÉÙ SQL ÓïŸä.
    //×¢Òâ: ÈôÔÚ 1 µÄÒ»¶ËµÄ @OneToMany ÖÐÊ¹ÓÃ mappedBy ÊôÐÔ, Ôò @OneToMany ¶ËŸÍ²»ÄÜÔÙÊ¹ÓÃ @JoinColumn ÊôÐÔÁË.

    //µ¥Ïò 1-n ¹ØÁª¹ØÏµÖŽÐÐ±£ŽæÊ±, Ò»¶š»á¶à³ö UPDATE ÓïŸä.
    //ÒòÎª n µÄÒ»¶ËÔÚ²åÈëÊ±²»»áÍ¬Ê±²åÈëÍâŒüÁÐ.
    @Test
    public void testOneToManyPersist(){
        Customer customer = new Customer();
        customer.setAge(18);
        customer.setBirth(new Date());
        customer.setCreatedTime(new Date());
        customer.setEmail("mm@163.com");
        customer.setLastName("MM");

        Order order1 = new Order();
        order1.setOrderName("O-MM-1");

        Order order2 = new Order();
        order2.setOrderName("O-MM-2");

        //œšÁ¢¹ØÁª¹ØÏµ
        customer.getOrders().add(order1);
        customer.getOrders().add(order2);

        order1.setCustomer(customer);
        order2.setCustomer(customer);

        //ÖŽÐÐ±£Žæ²Ù×÷
        entityManager.persist(customer);

        entityManager.persist(order1);
        entityManager.persist(order2);
    }

	*//**//**//**//*
	@Test
	public void testManyToOneUpdate(){
		Order order = entityManager.find(Order.class, 2);
		order.getCustomer().setLastName("FFF");
	}

	//²»ÄÜÖ±œÓÉŸ³ý 1 µÄÒ»¶Ë, ÒòÎªÓÐÍâŒüÔŒÊø.
	@Test
	public void testManyToOneRemove(){
//		Order order = entityManager.find(Order.class, 1);
//		entityManager.remove(order);

		Customer customer = entityManager.find(Customer.class, 7);
		entityManager.remove(customer);
	}

	//Ä¬ÈÏÇé¿öÏÂ, Ê¹ÓÃ×óÍâÁ¬œÓµÄ·œÊœÀŽ»ñÈ¡ n µÄÒ»¶ËµÄ¶ÔÏóºÍÆä¹ØÁªµÄ 1 µÄÒ»¶ËµÄ¶ÔÏó.
	//¿ÉÊ¹ÓÃ @ManyToOne µÄ fetch ÊôÐÔÀŽÐÞžÄÄ¬ÈÏµÄ¹ØÁªÊôÐÔµÄŒÓÔØ²ßÂÔ
	@Test
	public void testManyToOneFind(){
		Order order = entityManager.find(Order.class, 1);
		System.out.println(order.getOrderName());

		System.out.println(order.getCustomer().getLastName());
	}
	*//**//**//**//*

    *//**//**//**//**
     * ±£Žæ¶à¶ÔÒ»Ê±, œšÒéÏÈ±£Žæ 1 µÄÒ»¶Ë, ºó±£Žæ n µÄÒ»¶Ë, ÕâÑù²»»á¶à³ö¶îÍâµÄ UPDATE ÓïŸä.
     *//**//**//**//*
	*//**//**//**//*
	@Test
	public void testManyToOnePersist(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("gg@163.com");
		customer.setLastName("GG");

		Order order1 = new Order();
		order1.setOrderName("G-GG-1");

		Order order2 = new Order();
		order2.setOrderName("G-GG-2");

		//ÉèÖÃ¹ØÁª¹ØÏµ
		order1.setCustomer(customer);
		order2.setCustomer(customer);

		//ÖŽÐÐ±£Žæ²Ù×÷
		entityManager.persist(order1);
		entityManager.persist(order2);

		entityManager.persist(customer);
	}
	*//**//**//**//*

    *//**//**//**//**
     * Í¬ hibernate ÖÐ Session µÄ refresh ·œ·š.
     *//**//**//**//*
    @Test
    public void testRefresh(){
        Customer customer = entityManager.find(Customer.class, 1);
        customer = entityManager.find(Customer.class, 1);

        entityManager.refresh(customer);
    }

    *//**//**//**//**
     * Í¬ hibernate ÖÐ Session µÄ flush ·œ·š.
     *//**//**//**//*
    @Test
    public void testFlush(){
        Customer customer = entityManager.find(Customer.class, 1);
        System.out.println(customer);

        customer.setLastName("AA");

        entityManager.flush();
    }

    //ÈôŽ«ÈëµÄÊÇÒ»žöÓÎÀë¶ÔÏó, ŒŽŽ«ÈëµÄ¶ÔÏóÓÐ OID.
    //1. ÈôÔÚ EntityManager »ºŽæÖÐÓÐ¶ÔÓŠµÄ¶ÔÏó
    //2. JPA »á°ÑÓÎÀë¶ÔÏóµÄÊôÐÔžŽÖÆµœ²éÑ¯µœEntityManager »ºŽæÖÐµÄ¶ÔÏóÖÐ.
    //3. EntityManager »ºŽæÖÐµÄ¶ÔÏóÖŽÐÐ UPDATE.
    @Test
    public void testMerge4(){
        Customer customer = new Customer();
        customer.setAge(18);
        customer.setBirth(new Date());
        customer.setCreatedTime(new Date());
        customer.setEmail("dd@163.com");
        customer.setLastName("DD");

        customer.setId(4);
        Customer customer2 = entityManager.find(Customer.class, 4);

        entityManager.merge(customer);

        System.out.println(customer == customer2); //false
    }

    //ÈôŽ«ÈëµÄÊÇÒ»žöÓÎÀë¶ÔÏó, ŒŽŽ«ÈëµÄ¶ÔÏóÓÐ OID.
    //1. ÈôÔÚ EntityManager »ºŽæÖÐÃ»ÓÐžÃ¶ÔÏó
    //2. ÈôÔÚÊýŸÝ¿âÖÐÒ²ÓÐ¶ÔÓŠµÄŒÇÂŒ
    //3. JPA »á²éÑ¯¶ÔÓŠµÄŒÇÂŒ, È»ºó·µ»ØžÃŒÇÂŒ¶ÔÒ»žöµÄ¶ÔÏó, ÔÙÈ»ºó»á°ÑÓÎÀë¶ÔÏóµÄÊôÐÔžŽÖÆµœ²éÑ¯µœµÄ¶ÔÏóÖÐ.
    //4. ¶Ô²éÑ¯µœµÄ¶ÔÏóÖŽÐÐ update ²Ù×÷.
    @Test
    public void testMerge3(){
        Customer customer = new Customer();
        customer.setAge(18);
        customer.setBirth(new Date());
        customer.setCreatedTime(new Date());
        customer.setEmail("ee@163.com");
        customer.setLastName("EE");

        customer.setId(4);

        Customer customer2 = entityManager.merge(customer);

        System.out.println(customer == customer2); //false
    }

    //ÈôŽ«ÈëµÄÊÇÒ»žöÓÎÀë¶ÔÏó, ŒŽŽ«ÈëµÄ¶ÔÏóÓÐ OID.
    //1. ÈôÔÚ EntityManager »ºŽæÖÐÃ»ÓÐžÃ¶ÔÏó
    //2. ÈôÔÚÊýŸÝ¿âÖÐÒ²Ã»ÓÐ¶ÔÓŠµÄŒÇÂŒ
    //3. JPA »áŽŽœšÒ»žöÐÂµÄ¶ÔÏó, È»ºó°Ñµ±Ç°ÓÎÀë¶ÔÏóµÄÊôÐÔžŽÖÆµœÐÂŽŽœšµÄ¶ÔÏóÖÐ
    //4. ¶ÔÐÂŽŽœšµÄ¶ÔÏóÖŽÐÐ insert ²Ù×÷.
    @Test
    public void testMerge2(){
        Customer customer = new Customer();
        customer.setAge(18);
        customer.setBirth(new Date());
        customer.setCreatedTime(new Date());
        customer.setEmail("dd@163.com");
        customer.setLastName("DD");

        customer.setId(100);

        Customer customer2 = entityManager.merge(customer);

        System.out.println("customer#id:" + customer.getId());
        System.out.println("customer2#id:" + customer2.getId());
    }

    *//**//**//**//**
     * ×ÜµÄÀŽËµ: ÀàËÆÓÚ hibernate Session µÄ saveOrUpdate ·œ·š.
     *//**//**//**//*
    //1. ÈôŽ«ÈëµÄÊÇÒ»žöÁÙÊ±¶ÔÏó
    //»áŽŽœšÒ»žöÐÂµÄ¶ÔÏó, °ÑÁÙÊ±¶ÔÏóµÄÊôÐÔžŽÖÆµœÐÂµÄ¶ÔÏóÖÐ, È»ºó¶ÔÐÂµÄ¶ÔÏóÖŽÐÐ³ÖŸÃ»¯²Ù×÷. ËùÒÔ
    //ÐÂµÄ¶ÔÏóÖÐÓÐ id, µ«ÒÔÇ°µÄÁÙÊ±¶ÔÏóÖÐÃ»ÓÐ id.
    @Test
    public void testMerge1(){
        Customer customer = new Customer();
        customer.setAge(18);
        customer.setBirth(new Date());
        customer.setCreatedTime(new Date());
        customer.setEmail("cc@163.com");
        customer.setLastName("CC");

        Customer customer2 = entityManager.merge(customer);

        System.out.println("customer#id:" + customer.getId());
        System.out.println("customer2#id:" + customer2.getId());
    }

    //ÀàËÆÓÚ hibernate ÖÐ Session µÄ delete ·œ·š. °Ñ¶ÔÏó¶ÔÓŠµÄŒÇÂŒŽÓÊýŸÝ¿âÖÐÒÆ³ý
    //µ«×¢Òâ: žÃ·œ·šÖ»ÄÜÒÆ³ý ³ÖŸÃ»¯ ¶ÔÏó. ¶ø hibernate µÄ delete ·œ·šÊµŒÊÉÏ»¹¿ÉÒÔÒÆ³ý ÓÎÀë¶ÔÏó.
    @Test
    public void testRemove(){
//		Customer customer = new Customer();
//		customer.setId(2);

        Customer customer = entityManager.find(Customer.class, 2);
        entityManager.remove(customer);
    }

    //ÀàËÆÓÚ hibernate µÄ save ·œ·š. Ê¹¶ÔÏóÓÉÁÙÊ±×ŽÌ¬±äÎª³ÖŸÃ»¯×ŽÌ¬.
    //ºÍ hibernate µÄ save ·œ·šµÄ²»Í¬Ö®ŽŠ: Èô¶ÔÏóÓÐ id, Ôò²»ÄÜÖŽÐÐ insert ²Ù×÷, ¶ø»áÅ×³öÒì³£.
    @Test
    public void testPersistence(){
        Customer customer = new Customer();
        customer.setAge(15);
        customer.setBirth(new Date());
        customer.setCreatedTime(new Date());
        customer.setEmail("bb@163.com");
        customer.setLastName("BB");
        customer.setId(100);

        entityManager.persist(customer);
        System.out.println(customer.getId());
    }

    //ÀàËÆÓÚ hibernate ÖÐ Session µÄ load ·œ·š
    @Test
    public void testGetReference(){
        Customer customer = entityManager.getReference(Customer.class, 1);
        System.out.println(customer.getClass().getName());

        System.out.println("-------------------------------------");
//		transaction.commit();
//		entityManager.close();

        System.out.println(customer);
    }

    //ÀàËÆÓÚ hibernate ÖÐ Session µÄ get ·œ·š.
    @Test
    public void testFind() {
        Customer customer = entityManager.find(Customer.class, 1);
        System.out.println("-------------------------------------");

        System.out.println(customer);
    }
*/
    @Test
    public void testFind() {
        System.out.println("------------------Add-------------------");
        User user = new User();user.setFirstName("Metin");
        entityManager.persist(user);

    }
}

