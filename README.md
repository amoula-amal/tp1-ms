# tp1-ms
TP1 : Inversion de Contrôle (IoC) et Injection de Dépendances (DI)
# Compte-Rendu : TP1 - Inversion de Contrôle & Injection de Dépendances
## Contexte Universitaire
- Établissement :** Faculté des Sciences de Sfax (FSS)  
- Matière : Architecture MicroServices  
- Filière :*Master Professionnel  
- Année Universitaire :2026-2027  
- Enseignantes : Corinne Amel ZAYANI, Amal ABID  
Étudiant(e) :Amal Majdoub 
# Objectifs du TP
1. Comprendre et appliquer le concept de **couplage faible** en orienté objet.
2. Comprendre et illustrer l'Inversion de Contrôle  et l'Injection de Dépendances (DI).
3. Explorer l'évolution de l'injection de dépendances à travers 4 approches :
   - Activité 1-1 :Instanciation statique manuelle.
   - Activité 1-2 : Instanciation dynamique via fichier de configuration (`config.txt`) et réflexion (`Class.forName`).
   - Activité 1-3 : Utilisation du framework Spring avec configuration XML (`config.xml`).
   - Activité 1-4 : Utilisation des annotations Spring (`@Repository`, `@Service`, `@Autowired`).
## Introduction & Objectifs de l'Atelier
Structure du Projet
 1. Organisation du Projet
Pour garantir une séparation claire des responsabilités (architecture multicouche / MVC), l'application est découpée en 3 packages :
- `dao` : Regroupe l'abstraction des données (`IDao`) et sa concrétisation (`DaoIMP`).
- `metier` : Contient la logique applicative (`IMetier`, `MetierIMP`).
- `presentation` : Contient les différentes classes de test illustrant chaque étape
  <img width="510" height="716" alt="image" src="https://github.com/user-attachments/assets/eab3c0c5-0869-4a2c-8d79-6a4b8f5173b0" />
Activité 1-1 : Injection Statique & Couplage Faible
 2. Travail Réalisé et Analyse des Résultats
 1. Principe
- La classe `MetierIMP` est liée à l'interface `IDao` et non à la classe concrète `DaoIMP`.
- Dans `PresentationStatique`, nous instancions les deux classes statiquement à l'aide de l'opérateur `new`, puis nous injectons la dépendance via le setter `setDao()`.

 2. Code Source
🔹 Activité 1-1 : Couplage Faible et Instanciation Statique
Démarche :
Dans cette première étape, nous avons relié la classe `MetierIMP` à l'interface `IDao` au lieu d'une classe concrète. Dans `PresentationStatique`, nous créons nous-mêmes les instances avec le mot-clé `new`, puis nous lions le métier à son DAO via la méthode `setDao(dao)`.

// Dans presentation.PresentationStatique
// Instanciation directe et injection manuelle
IDao dao = new DaoIMP();
IMetier metier = new MetierIMP();
((MetierIMP) metier).setDao(dao);
System.out.println(metier.calcul()); // Affiche 21.0 (3 * 7)
System.out.println(metier.calcul()); // Résultat attendu : 21.0

<img width="1060" height="355" alt="image" src="https://github.com/user-attachments/assets/4e3bad06-2259-416a-a9fc-7db9bc35b5f5" />

4. Réponses aux Questions
- Q9. Expliquez comment cela illustre le couplage faible :**  
  `MetierIMP` dépend uniquement du contrat défini par l'interface `IDao` (`getValue()`). Elle ne connaît aucune classe concrète (`DaoIMP`). Cela permet d'interchanger l'implémentation de la couche d'accès aux données (base de données, web service, mock de test) sans changer une seule ligne de code dans `MetierIMP`.
- Q10. Le principe d'Inversion de Contrôle est-il respecté ? Justifier :  
  **Non.** C'est la classe principale `PresentationStatique` qui contrôle directement la création des objets avec l'opérateur `new` et effectue manuellement le câblage avec `setDao()`. Le contrôle n'a pas été inversé.
- Q11. Que faudrait-il modifier pour éviter que la classe de présentation contrôle directement la création des objets ?**  
  Il faut déléguer la responsabilité de la création et de l'assemblage des dépendances à une entité externe : soit par instanciation dynamique via réflexion (Activité 1-2), soit par un conteneur IoC comme Spring (Activités 1-3 et 1-4).
- Analyse & Réponses aux questions :
Illustration du couplage faible (Q9) : 
  La classe `MetierIMP` ignore totalement quelle classe implémente concrètement le DAO (qu'il s'agisse de JDBC, d'un fichier ou d'une API distante). Elle dépend uniquement du contrat `getValue()`. Le code métier reste donc stable et intègre même si la source de données évolue.
Respect du principe IoC (Q10) :
  **Non**, à ce stade l'IoC n'est pas appliqué. C'est notre programme principal (`PresentationStatique`) qui contrôle l'instanciation des objets et orchestre leur liaison. L'autorité de contrôle reste du côté du développeur.
Amélioration nécessaire (Q11) : 
  Pour affranchir la couche présentation de ce contrôle direct, il faut externaliser le choix des classes dans un fichier de configuration et confier leur instanciation à un moteur dynamique ou à un conteneur IoC.
  - Les noms complets des classes concrètes sont placés dans un fichier de configuration externe `config.txt` :
  ```text
  dao.DaoIMP
  metier.MetierIMP
  ```
- L'application lit ce fichier à l'exécution, charge les classes avec `Class.forName()`, crée les instances avec `newInstance()` et injecte la dépendance par réflexion via `Method.invoke()`.
 Démarche :
Afin de ne plus écrire de noms de classes "en dur" dans le code, nous externalisons les classes à charger dans `config.txt` :
```text
dao.DaoIMP
metier.MetierIMP
```
La classe `PresentationDynamique` lit ces chaînes de caractères, charge les classes via `Class.forName()`, crée les instances avec `.getDeclaredConstructor().newInstance()`, puis injecte la dépendance par réflexion (`Method.invoke`).
### 2. Code Source
```java
// Lecture depuis config.txt et chargement dynamique
Class<?> cDao = Class.forName(daoClassName);
IDao dao = (IDao) cDao.getDeclaredConstructor().newInstance();
Class<?> cMetier = Class.forName(metierClassName);
IMetier metier = (IMetier) cMetier.getDeclaredConstructor().newInstance();
// Injection par réflexion via la méthode setDao
Method setDao = cMetier.getMethod("setDao", IDao.class);
setDao.invoke(metier, dao);
System.out.println(metier.calcul()); // Affiche 21.0
// Injection dynamique sans dépendance de compilation avec les implémentations
Method setDaoMethod = cMetier.getMethod("setDao", IDao.class);
setDaoMethod.invoke(metier, dao);
```
<img width="1774" height="901" alt="image" src="https://github.com/user-attachments/assets/b0db1d0e-9355-4d49-8147-11581e13cdd1" />
Analyse & Réponses aux questions :
Caractère dynamique de l'injection (Q7) : 
  Aucun constructeur d'implémentation n'est appelé de façon explicite à la compilation. Tout le processus (découverte, chargement mémoire et liaison) est déporté à l'exécution (runtime).
Rapprochement avec l'IoC (Q8) :
  On satisfait le principe *Open/Closed* (ouvert à l'extension, fermé à la modification). Si nous concevons un nouveau module DAO (`DaoV2`), il suffit de mettre à jour le fichier texte sans avoir à recompiler l'application.
Bilan critique (Q9) :  
  - *Points forts :* Grande souplesse, extensibilité aisée.
  - *Inconvénients :* Code technique lourd à maintenir, manipulation délicate de l'API Reflection, vulnérabilité aux fautes de frappe (erreurs visibles uniquement au runtime).
---
🍃 Activité 1-3 : Spring Framework avec Configuration XML
🔹 Activité 1-3 : Automatisation via Spring et Configuration XML
 1. Principe
- La déclaration des composants (beans) et de leurs dépendances est externalisée dans `src/main/resources/config.xml`.
- Le conteneur Spring (`ClassPathXmlApplicationContext`) lit ce fichier, instancie les beans et injecte automatiquement les dépendances par setter.
 2. Fichier de Configuration XML (`config.xml`)
 Démarche :
Nous exploitons ici le conteneur léger de Spring. Le câblage des composants est confié au fichier `config.xml` :
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       https://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean id="dao" class="dao.DaoIMP"/>
    <bean id="metier" class="metier.MetierIMP">
        <property name="dao" ref="dao"/>
    </bean>
</beans>
<bean id="dao" class="dao.DaoIMP"/>
<bean id="metier" class="metier.MetierIMP">
    <property name="dao" ref="dao"/>
</bean>
```
Dans `PresentationAvecSpring`, il suffit d'initialiser `ClassPathXmlApplicationContext` et de récupérer le bean métier prêt à l'emploi.
<img width="1913" height="976" alt="image" src="https://github.com/user-attachments/assets/b2d077a8-b6a5-47da-830a-d808c3683869" />

  - `@Service` sur `MetierIMP` (couche logique métier).
  - `@Autowired` sur l'attribut `private IDao dao` pour injecter automatiquement la dépendance.
- Démarrage du conteneur avec scan automatique des packages : `new AnnotationConfigApplicationContext("dao", "metier")`.
 Démarche :
Nous nous affranchissons du fichier XML au profit des annotations de stéréotypes :
- `@Repository` sur `DaoIMP` pour identifier le composant de données.
- `@Service` sur `MetierIMP` pour désigner le service métier.
- `@Autowired` sur la référence `dao` pour ordonner l'injection automatique.
2. Code Source
La classe `PresentationAvecAnnotation` initialise le contexte en lui indiquant les packages à analyser :
```java
// dao/DaoIMP.java
@Repository
public class DaoIMP implements IDao { ... }
// metier/MetierIMP.java
@Service
public class MetierIMP implements IMetier {
    @Autowired
    private IDao dao;
    ...
}
ApplicationContext context = new AnnotationConfigApplicationContext("dao", "metier");
IMetier metier = context.getBean(IMetier.class);
System.out.println(metier.calcul()); // 21.0
```
<img width="1912" height="947" alt="image" src="https://github.com/user-attachments/assets/7fb8492c-1bcf-43ec-a5e7-4ae71fc7ce42" />


4. Réponses aux Questions
- Q5. Expliquez comment Spring gère l'injection de dépendances à l'aide des annotations :* 
  Spring scanne les packages spécifiés, instancie les classes annotées par `@Repository` et `@Service` comme beans, et résout automatiquement le lien `@Autowired` en injectant l'instance compatible avec le type `IDao`.
- Q6. Pourquoi cette solution est-elle plus maintenable que le XML ? En quoi représente-t-elle une application complète de l'IoC ?
  - Maintenabilité :Configuration déclarée au plus près du code, support total de l'autocomplétion et du refactoring de l'IDE (évite les erreurs de chaînes dans le XML).  
  - *IoC complet :Application totale du principe *"Don't call us, we'll call you"*. Le développeur déclare simplement les composants, et Spring orchestre l'ensemble de l'application.
 Analyse & Réponses aux questions :
Fonctionnement de l'injection par annotations (Q5) :
  Au démarrage, Spring procède au scan (component-scan) des packages cibles. Lorsqu'il repère les annotations `@Repository` et `@Service`, il instancie les beans associés. En rencontrant `@Autowired`, il recherche un bean de type compatible (`IDao`) dans son conteneur et l'affecte automatiquement.
Avantages par rapport au XML & Portée IoC (Q6) :
  - *Maintenabilité :* Moins de verbosité, configuration contextuelle au code, support immédiat du refactoring par l'IDE (vérification à la compilation).
  - *Aboutissement de l'IoC :* C'est l'illustration parfaite du principe *"Don't call us, we'll call you"*. Le développeur décrit uniquement ses besoins via des annotations, et le framework pilote l'ensemble de l'architecture.
---
  Tableau Récapitulatif
 3. Synthèse des Approches
| Activité | Qui crée et assemble les objets ? | Mécanisme utilisé | Couplage | IoC Respecté ? |
| Étape | Gestionnaire du cycle de vie | Détection des dépendances | Niveau de Couplage | Degré d'IoC |
| :--- | :--- | :--- | :--- | :--- |
| **1-1 Statique** | Le programmeur | `new` + `setDao()` | Faible au niveau Métier / Fort au niveau Présentation |  Non |
| **1-2 Dynamique** | Le programme via configuration | `config.txt` + `Class.forName()` + Réflexion | Faible partout |  Partiel |
| **1-3 Spring XML** | Le Conteneur Spring | `config.xml` (`<bean>`, `<property>`) | Faible partout |  Oui |
| **1-4 Spring Annotations** | Le Conteneur Spring | `@Repository`, `@Service`, `@Autowired` | Faible partout |  Oui (Complet) |
| **1-1 Statique** | Développeur (code Java) | Manuelle (`setDao`) | Fort dans la couche cliente |  Nul |
| **1-2 Dynamique** | Développeur (via `config.txt`) | Réflexion (`Method.invoke`) | Faible |  Partiel |
| **1-3 Spring XML** | Conteneur Spring | Fichier `config.xml` | Faible |  Total |
| **1-4 Spring Annotations** | Conteneur Spring | Annotations (`@Autowired`) | Faible |  Total & Moderne |
---
 Instructions pour Cloner et Exécuter
 4. Conclusion
1. Cloner le dépôt :
   ```bash
   git clone https://github.com/VOTRE_USERNAME/NOM_DU_REPO.git
   ```
2. Ouvrir le projet :
   - Ouvrir IntelliJ IDEA ➜ File ➜ Open ➜ Choisir le dossier du projet.
   - Attendre la synchronisation Maven.
3. Exécuter :
   - Lancer l'une des classes `main` dans `src/main/java/presentation/`.
À travers ce TP, nous avons pu constater l'intérêt concret de l'**Inversion de Contrôle** dans une architecture logicielle. Partant d'un code fortement couplé et peu modulable, l'introduction des interfaces a permis d'isoler les responsabilités. L'utilisation finale de **Spring avec annotations** offre la solution la plus élégante : un code concis, facilement testable, et totalement ouvert aux évolutions futures sans modifier l'existant.
