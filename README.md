# Editorial Management Access Control

> Developed for Computer Security Project 2 by Travis Rau


## Basic Roles
- Author
- Editor
- Associate Editor
- Reviewer
- Administrator


## Actions Available Within the Editorial Management System
- Owner
- Edit
- Read
- Submit
- Send
- Accept
- Review
- Consider Reviews


Authors have Owner/Edit/Read/Submit on manuscripts they created. They have no capabilities on other manuscripts (unless they have an Author/Associate Editor  or Author/Reviewer role)


Editors have Read/Send/Review/Consider Reviews on manuscripts that have been submitted. Once they review a manuscript, the only capability they will have is consider reviews. Once an editor performs the consider reviews action all access is lost. The send capability gives a reviewer or associate editor the accept capability


Associate Editors have Read/Send/Review/Consider Reviews on manuscripts they have been invited to and accepted. An un-invited associate editor will have no capabilities and an invited associate editor will have the accept capability. Once using the accept capability, the associate editor gains Read/Send/Review/Consider Reviews. Similarly to the Editor, once the manuscript is reviewed the only access left is consider reviews. Consider Reviews also removes access once used

Reviewers have Read/Review on manuscripts they have been invited to and accepted. Once the review is used all access is lost

Administrators have all possible actions on each manuscript in the system