# ACME Medical REST API (Jakarta EE)  

A secure RESTful API for managing medical data with role-based access control, built using Jakarta EE, JPA, and Maven.  

## 📌 Features  
- **CRUD Operations**: Create, read, update, and delete medical records via REST endpoints.  
- **JPA Entity Mapping**: Database schema mapped to Java POJOs with annotations.  
- **Role-Based Security**: Users assigned `ADMIN_ROLE` or `USER_ROLE` for endpoint access control.  
- **Automated Testing**: JUnit tests with Maven Surefire plugin for validation.  
- **Postman Integration**: Pre-configured collection for API testing.  

## 🛠 Technologies  
- **Backend**: Jakarta EE (JAX-RS, JPA, EJB)  
- **Security**: JEE Role-Based Authentication  
- **Build Tool**: Maven  
- **Testing**: JUnit, Postman  

## 🚀 Setup  
1. **Prerequisites**:  
   - Java 8+  
   - Eclipse/Jakarta EE-compatible IDE  
   - Maven 3.6+  
   - PostgreSQL/Compatible DB (configured in `persistence.xml`)  

2. **Run Locally**:  
   ```bash
   git clone [repo-url]  
   cd REST-ACMEMedical  
   mvn clean install  
   # Deploy to Jakarta EE server (e.g., Payara/GlassFish)

   src/  
├── main/  
│   ├── java/.../entities/       # JPA-mapped POJOs (e.g., Patient, SecurityUser)  
│   ├── java/.../rest/           # REST endpoints  
│   ├── java/.../services/       # Business logic (EJBs)  
│   └── resources/               # META-INF/persistence.xml, security config  
└── test/                        # JUnit tests  

🔒 Security Roles
Role	Access Permissions
ADMIN_ROLE	Full CRUD + user management
USER_ROLE	Read-only + limited writes


