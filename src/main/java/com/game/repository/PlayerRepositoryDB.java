package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PreDestroy;

import java.util.List;
import java.util.Optional;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository, AutoCloseable {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Configuration configuration = new Configuration();
        configuration.configure();
        configuration.addAnnotatedClass(Player.class);
        sessionFactory = configuration.buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            NativeQuery<Player> query = session.createNativeQuery("SELECT * FROM rpg.player", Player.class);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            tx.commit();
            return query.list();
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }

    @Override
    public int getAllCount() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            long playersTotal = session.createNamedQuery(Player.COUNT_PLAYERS, Long.class)
                    .getSingleResult();
            tx.commit();
            return Math.toIntExact(playersTotal);
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }

    @Override
    public Player save(Player player) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(player);
            tx.commit();
            return player;
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }

    @Override
    public Player update(Player player) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.update(player);
            tx.commit();
            return player;
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            Player player = session.find(Player.class, id);
            tx.commit();
            return Optional.ofNullable(player);
        } catch (Exception e){
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(Player player) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try{
            session.remove(player);
            tx.commit();
        } catch (Exception e){
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }

    @Override
    public void close() {
        sessionFactory.close();
    }
}