#CRYPTOMAN

CRYPTOMAN is a user-friendly digital cryptocurrency trading platform. It allows users to effortlesly buy and sell crypto with the
push of a button. CryptoMan implements API for the latest crypto prices, the API being refreshed every 60 seconds. It includes
feautures such as crypto buying and selling, transaction history, reseting your account back to the starting position and many more.

Team Members
Ivan Georgiev - ivan200126y

Features
Buy & Sell crypto - Buy based on the $ amount you want to purchase, not the exact number of shares. 
Transaction History - View all of your transactions, open and closed
Open Transactions - View your current open positions, along with the purchase price, current price and the profit or the 
loss you have on the position.
Crypto Prices - View the real-time prices on the top-20 crypto currencies around the world, with the prices being updated
every minute.

Installation
Clone the repository on your computer and install the dependencies in build.gradle
Make sure you have JDK 17 and MariaDB installed
Run the create.sql and insert-data.sql script located at /Virtual Wallet/db
Access the application at this url - http://localhost:3308

Configuration
The current configuration has changed and will not work for you due to privacy reasons.
That's why before running the app, in order to use some of the functionalities you must configure 
application.properties.

Navigate to:
src/main/resources/application.properties

And modify the following properties:
# Database Configuration
spring.datasource.url=jdbc:mariadb://localhost:3306/virtual_wallet
spring.datasource.username=root
spring.datasource.password={yourpassword}

# Email verification
spring.mail.username={sender_email_username}
spring.mail.password={sender_email_password}

# API Keys
cloudinary.cloud-name={your_cloud_name}
cloudinary.api-key={your_api_key}
cloudinary.api-secret={your_api_secret}

Login Details
The provided credentials are for testing purposes only!
It's essential to inform you that the application encripts passwords,
ensuring they are not kept in their original form.

Admin: username: johndoe, password: johndoe
User: username: alicesmith, password: alicesmith

Technologies
Java 17
Spring Boot
Spring MVC
Spring Data JPA
Gradle
SQL
MariaDB
Thymeleaf
HTML5
CSS
JUnit
Mockito


Functionalities
Main Page
