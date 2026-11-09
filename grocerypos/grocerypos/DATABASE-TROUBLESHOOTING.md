# Grocery POS Database Troubleshooting

## Summary

The application code and login page were able to start, but the original MySQL connection failed authentication. The app was temporarily switched to an in-memory H2 database so the login page could be tested. MySQL was then configured with a separate application user and the project has since been switched back to MySQL.

## What happened

1. The original Spring datasource configuration connected to MySQL at `localhost:3306`, targeting the `grocerypos` database as `root`.
2. The first startup log reported:

   ```text
   Access denied for user 'root'@'localhost' (using password: YES)
   ```

   This means the MySQL server was reachable, but it rejected the configured credentials. Hibernate's later “Unable to determine Dialect” message was a consequence of the failed connection, not the underlying problem.
3. Workbench was opened to a model file, which is not the same as testing a saved MySQL server connection. Workbench can also reuse saved credentials, so opening it without a prompt does not establish that the account has a blank password.
4. Clearing the app password produced a second error:

   ```text
   Access denied for user 'root'@'localhost' (using password: NO)
   ```

   This confirmed that MySQL did not accept a passwordless `root` login.
5. H2 was added as a temporary development database. The login page started with H2, but H2's in-memory data is discarded when the application stops. This only demonstrated that the web app could run without the MySQL connection; it did not fix MySQL.
6. In MySQL Workbench, the `grocerypos` database and a dedicated `grocerypos_app` user were created, and privileges were granted. The initial example password placeholder was entered literally, so it was important to replace it with a private password before using the account.
7. The project configuration was changed back to MySQL using `grocerypos_app`. No password is included in this document.

## Current configuration

`src/main/resources/application.properties` is currently configured for:

- MySQL on `localhost:3306`
- Database: `grocerypos`
- User: `grocerypos_app`
- Hibernate schema update mode: `update`

The password is present in the local configuration but is intentionally omitted here. The project also has the H2 runtime dependency; it is not the active database while the datasource URL points to MySQL.

## What was misunderstood

- “Workbench opens immediately” does not prove that the MySQL user has no password; Workbench may use a saved password or may simply be showing a model.
- The `.mwb` model and the live MySQL server/database are separate things.
- A successful login page under H2 does not verify the MySQL connection.
- Database names and login credentials must match the live MySQL server. The original `root` credentials did not work for this application.
- A sample password in setup instructions is a placeholder, not a recommended real password. If it was used literally, change it to a unique private value.

## Verify MySQL is working

1. Ensure MySQL Server is running.
2. In Workbench, test the connection and verify the `grocerypos` schema is present.
3. Ensure the same username and password are configured in `application.properties` as the MySQL application user. Never share or commit the actual password.
4. Restart the Spring Boot application.
5. Check the startup output for a successful datasource/Hikari connection and a final `Started GroceryposApplication` message. If it fails, use the first MySQL connection error as the primary diagnostic.
6. Open `http://localhost:8080` and confirm the application responds. This verifies the app is running; inspect the MySQL schema separately to confirm data is being stored there.

For a more secure setup, keep the database password outside the source file (for example, in an environment variable) and reference it from Spring configuration.
