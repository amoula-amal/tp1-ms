package metier;

import dao.IDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetierIMP implements IMetier {

    @Autowired
    private IDao dao;   // dépendance vers l'INTERFACE : couplage faible

    public MetierIMP() {
    }

    public MetierIMP(IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        double val = dao.getValue();
        return val * 7;
    }

    // Setter utilisé pour l'injection
    public void setDao(IDao dao) {
        this.dao = dao;
    }
}
