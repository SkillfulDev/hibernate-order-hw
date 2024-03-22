package mate.academy.dao.impl;

import java.util.Optional;
import mate.academy.dao.OrderDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.Order;
import mate.academy.model.User;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Dao
public class OrderDaoImpl implements OrderDao {
    @Override
    public Order add(Order order) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(order);
            transaction.commit();
            return order;
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can`t add order "
                    + order + " to DB", ex);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Optional<Order> getByUser(User user) {
        String query = "FROM Order o "
                + "left join fetch o.tickets t "
                + "left join fetch t.movieSession m"
                + " left join fetch t.user"
                + " left join fetch m.cinemaHall"
                + " left join fetch m.movie"
                + " where  o.user = :user";
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            Query<Order> orderQuery = session.createQuery(query, Order.class);
            orderQuery.setParameter("user", user);
            return Optional.ofNullable(orderQuery.getSingleResult());
        }
    }
}
