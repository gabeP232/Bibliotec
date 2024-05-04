# How to Build & Run


1. Download Java 21. [Download Link](https://www.azul.com/downloads/?version=java-21-lts&package=jdk#zulu) and make sure it is installed and available in your `PATH`.
2. Install MySQL and start the server and have it run on `localhost:3306` (localhost port 3306) with user `root`
3. If there is a password for the root user, set the environment variable `DB_PASSWORD` to the password
    ```
    export DB_PASSWORD=your_password
    ```
4. Clone the repository
    ```
    git clone https://github.com/gabeP232/Bibliotec.git
    cd Bibliotec
    ```
5. Run the following command to run the project. (it may take some time to build on the first run)
    ```
    ./gradlew run
    ```
It will open a window with the application.

