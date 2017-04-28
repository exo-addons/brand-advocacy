brand-advocacy
==============
** How to build this addon **

To build this addon, you need to be familiar to maven.
+ To get ths whole project to your local: git clone https://github.com/exo-addons/brand-advocacy.git
+ To compile the project,in the brand advocacy directory: mvn clean install
+ After compiling, in the directory: brand-advocacy/bundle/target you must have brand-advocacy-bundle-x.zip file. This file contains 2 jar and 2 war
- brand-advocacy-lib-{x}.jar
- brand-advocacy-webapp.war
- brand-advocacy-extension.war

** How to install it **
+ All you need is just to unzip the bundle zip file and place the jar files in lib/ and war file in the webapp/ directory.
+ Start your web server
+ Addon will create automatically 1 admin menu item: /portal/intranet/brand-advocacy
+ To display frontend, you can add the portlet named "Juzu Brand Advocacy Frontend Portlet" wherever you want.

** Brand Advocacy Add-on currently has some main functions **

1. Frontend: allows user to participate a mission.
2. Backend: allows manager to manage.

a/ list of managers:
- add or remove a manager.
- his role.
- email notification.

b/ list of missions:
- add or remove or enable/disable a mission.
- its probability to be displayed in frontend.
- add or remove or enable/disable the propositions of a mission

c/ list of participant
- search by username, status
- update participant following the flow of logistic: wait for validating -> validate -> wait for shipping -> shipped or reject -> replay

d/ Program:
- Update the title.
- update the banner.
- update the sender of email

** How does it work **

To display active mission in frontend, admin needs to go to backend, and submit a program.
In that program admin needs to create at least one mission and one proposition.
When having active missions, user can participate to one or many missions and he cannot replay the mission that he already finishes.

According to the actions  of user, a participant can have 3 status open, in progress, wait for validating:
- Open: when user just sees one mission
- In progress: when user starts his mission
- wait for validating: when user submits the addresses form to finish his mission.

When a mission

- is submitted: the participant will receive a confirmation email and the validator will also receive a notification email that tells them which mission they should check to validate it.

- is validated: the shipping managers will receive a notification email that tells them about lead’s information to send the gift.

- is shipped: the participant will be notified by an email telling him that his mission is validated and the gift is on the way.

** More info about brand advocacy **
http://blog.exoplatform.com/en/2015/01/29/reward-your-community-and-boost-engagement-with-the-advocacy-add-on