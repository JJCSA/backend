--liquibase formatted sql

--changeset tejas:update-email-templates

UPDATE email_templates
SET email_body = '<p> Jai Jinendra <span th:text="${firstName}"></span>!</p>' ||
                 '<p> Your profile was rejected due to following reason: <span th:text="${rejectReason}"></span></p>' ||
                 '<p>If you feel this is an error, Please reach out to us by sending an email at <span style="color: #244f90 !important;text-decoration: none;">jjcsausa@gmail.com</span> </p>'
WHERE template_name = 'REJECTED';

UPDATE email_templates
SET email_body = '<p>Jai Jinendra!<br><br>🌟 We appreciate your registration on our website, and we&rsquo;re pleased to inform you that your profile has been approved! 🌟</p>' ||
                 '<p>✅ Registration Complete! ✔️</p>' ||
                 '<p><strong style="background-color:#ff0">Next Step: Complete Your Profile 📌</strong></p>' ||
                 '<p>This will enable you to utilize the JJC SEARCH ENGINE feature 📊, FAQ&rsquo;s 📚 and ensure that you receive emails and notifications about our upcoming EVENTS 📅.</p>' ||
                 '<p>To make your transition smoother and help you build a strong network, we&rsquo;ve set up three WhatsApp groups based on different time zones. These groups are designed to connect you with local students and professionals who understand the unique challenges and opportunities that come with studying and living in the USA.</p>' ||
                 '<p>Here are the three WhatsApp groups you can join, depending on your time zone:</p>' ||
                 '<p>1. Eastern Time Zone Group</p>' ||
                 '<p>2. Central Time Zone Group</p>' ||
                 '<p>3. Pacific Time Zone Group</p>' ||
                 '<p>To join your respective WhatsApp group, simply send a message on WhatsApp<a href="https://wa.me/19093336349" style="color:#244f90!important;text-decoration:none">(+1 909-333-6349 📞)</a>.</p>' ||
                 '<p><strong style="font-weight:700">(Please allow 24 to 48 hrs for a reply, we will get back as soon as possible) 🕒</strong></p>' ||
                 '<p>Wish to volunteer? Need assistance? Have questions? You can reach out to us by sending an email at <span style="color:#244f90!important;text-decoration:none">jjcsausa@gmail.com 📧</span>or Whatsapp(+1 909-333-6349 📞).</p>' ||
                 '<p>Thank you for your engagement, and we look forward to your active participation in our community. 🙌🤝</p>' ||
                 '<img style="width:100%" data-imagetype="External" src="https://jjc-email-template.s3.amazonaws.com/image+7.png">'
WHERE template_name = 'APPROVED';

UPDATE email_templates
SET email_body = '<p> Jai Jinendra <span th:text="${firstName}"></span>!</p>' ||
                 '<p>Your Password is updated. If it is not updated by you, Please reach out immediately to jjcsausa@gmail.com</p>'
WHERE template_name = 'PASSWORD_UPDATE';

UPDATE email_templates
SET email_body = '<p> Jai Jinendra <span th:text="${firstName}"></span>!</p>' ||
                 '<p>Your Password Update Request Failed. Please reach out to jjcsausa@gmail.com for updating the password or use forgot password instead of Reset.</p>'
WHERE template_name = 'PASSWORD_UPDATE_FAILED';

UPDATE email_templates
SET email_body = '<p> Jai Jinendra <span th:text="${firstName}"></span>!</p>' ||
                 '<p>Your Profile is updated. If it is not updated by you, Please reach out immediately to jjcsausa@gmail.com</p>'
WHERE template_name = 'PROFILE_UPDATE';

UPDATE email_templates
SET email_body = '<p> Jai Jinendra!</p>' ||
                 '<p>Please use below Information to reset your password.</p>' ||
                 '<p>Email: <span th:text="${email}"/></p>' ||
                 '<p>URL : <span th:text="${link}"/></p>' ||
                 '<p>Temp Password : <span th:text="${tempPassword}"/>'
WHERE template_name = 'FORGOT_PASSWORD';

UPDATE email_templates
SET email_body = '<p>🌟 Jai Jinendra,<span th:text="${firstName}"></span>! 🌟</p>' ||
                 '<p>Your Profile is under review by our admin team! You will get email updates once your profile is approved. 📬</p>' ||
                 '<p>Welcome to Jain Jagruti Centre, USA (JJC USA). JJC USA, a branch of Jain Jagruti Centre Central Board & Charitable Trust, is a non-profit organization formed in 2019. It is formed by a group of volunteers dedicated towards implementing diverse initiatives that contribute to the overall upliftment of its members. 🌍</p>' ||
                 '<p>We are a growing organization of 1200+ Jain students and working professionals in the US. We are building a platform with the primary purpose of helping Jain students and working professionals in the United States with the aim to provide: 📚</p>' ||
                 '<span style="color:#4a4a4a;line-height:19px;margin-top:0;padding:0">These include:</span>' ||
                 '<ul style="color:#4a4a4a;line-height:19px;margin-top:0;margin-bottom:25px">' ||
                 '<li>Opportunities for networking 🤝</li>' ||
                 '<li>Support individuals’ professional & career growth 🚀</li>' ||
                 '<li>Assist them in building a strong community 👥</li>' ||
                 '</ul>' ||
                 '<p>We have 5 pillars that function together to support our member community. 💪</p>' ||
                 '<span style="color:#4a4a4a;line-height:19px;margin-top:0;padding:0">They are:</span>' ||
                 '<ul style="color:#4a4a4a;line-height:19px;margin-top:0;margin-bottom:25px">' ||
                 '<li>Alumni Welfare Committee 🎓</li>' ||
                 '<li>Student Welfare Committee 📚</li>' ||
                 '<li>Social Events Committee 🎉</li>' ||
                 '<li>Website Development 💻</li>' ||
                 '<li>Marketing and Communication 📢</li>' ||
                 '</ul>' ||
                 '<p>To stay connected and interact with our members, you can join the JJC USA Telegram Group and Whatsapp Regional Groups. To be added to these groups, you can text us on our WhatsApp number +1 909-333-6349 📱. You can find us on these channels.</p>' ||
                 '<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" class="border-complete deviceWidth">
                    <tr>
                        <td align="center" valign="top" id="socials">
                            <table id="social-icons" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td><a href="https://www.instagram.com/jjcusa/"><img style="padding-left:10px;display:block" src="https://jjc-email-template.s3.amazonaws.com/image+3.png" width="32" height="32"></a></td>
                                    <td><a href="https://www.youtube.com/channel/UCERP8n9IzMXT6eqZtUQVovw?view_as=subscriber"><img style="padding-left:10px;display:block" src="https://jjc-email-template.s3.amazonaws.com/image+5.png" width="32" height="32"></a></td>
                                    <td><a href="https://www.linkedin.com/in/jjc-us-student-association/"><img style="padding-left:10px;display:block" src="https://jjc-email-template.s3.amazonaws.com/image+6.png" width="32" height="32"></a></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>' ||
                 '<p>Wish to volunteer? Need assistance? Have questions? You can reach out to us by sending an email at<a href="mailto:jjcsausa@gmail.com" style="color:#244f90!important;text-decoration:none">jjcsausa@gmail.com 📧</a>or on Whatsapp 📞</p>' ||
                 '<p>Again, we welcome you to this organization and if you have any questions, please do not hesitate to reach out to us. 🤗</p>' ||
                 '<img style="width:100%" data-imagetype="External" src="https://jjc-email-template.s3.amazonaws.com/image+7.png" alt="Community Image">'
WHERE template_name = 'REGISTRATION';

