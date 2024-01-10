package com.jjcsa.util;

import com.jjcsa.dto.UserDTO;
import com.jjcsa.model.Education;
import com.jjcsa.model.WorkEx;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.List;

@UtilityClass
public class CsvGeneratorUtil {

    public String generateCSV(List<UserDTO> users) {
        StringBuilder csvContent = new StringBuilder();
        String CSV_HEADER = "ID,Email,Education,WorkExperience,LastUpdatedDate,ApprovedDate,FirstName,MiddleName" +
                ",LastName,MobileNumber,ContactMethod,CommunityName,Status,Street,City,State,Zip,DateOfBirth" +
                ",SocialMediaPlatform,VolunteeringInterest,LoanTaken,LoanOrganization,LinkedInUrl,Description" +
                ",ContactShared,Country,isUserStudent,UserRole,Gender,AboutMe,isRegionalContact";
        csvContent.append(CSV_HEADER).append("\n");

        for (UserDTO user : users) {
            csvContent.append(user.getId()).append(",")
                    .append(user.getEmail()).append(",");

            // flatten education list
            if (CollectionUtils.isEmpty(user.getEducationList())) {
                csvContent.append(","); // empty value
            } else {
                csvContent.append("\""); // enclose all Education values in ""
                for (int i = 0; i < user.getEducationList().size(); i++) {
                    if (i > 0) {
                        csvContent.append(", ");
                    }
                    Education education = user.getEducationList().get(i);
                    csvContent.append(education.getUniversityName())
                            .append(" - ")
                            .append(education.getDegree());
                }
                csvContent.append("\",");
            }

            // flatten workexp list
            if (CollectionUtils.isEmpty(user.getWorkExperience())) {
                csvContent.append(","); // empty value
            } else {
                csvContent.append("\""); // enclose all Education values in ""
                for (int i = 0; i < user.getWorkExperience().size(); i++) {
                    if (i > 0) {
                        csvContent.append(", ");
                    }
                    WorkEx workEx = user.getWorkExperience().get(i);
                    csvContent.append(workEx.getCompanyName())
                            .append(" - ")
                            .append(workEx.getRole());
                }
                csvContent.append("\",");
            }

            csvContent.append(user.getLastUpdatedDate()).append(",")
                    .append(user.getApprovedDate()).append(",")
                    .append(user.getFirstName()).append(",")
                    .append(user.getMiddleName()).append(",")
                    .append(user.getLastName()).append(",")
                    .append(user.getMobileNumber()).append(",")
                    .append(user.getContactMethod()).append(",")
                    .append(user.getCommunityName()).append(",")
                    .append(user.getUserStatus()).append(",")
                    .append(user.getStreet()).append(",")
                    .append(user.getCity()).append(",")
                    .append(user.getState()).append(",")
                    .append(user.getZip()).append(",")
                    .append(user.getDateOfBirth()).append(",")
                    .append(user.getSocialMediaPlatform()).append(",")
                    .append(user.getVolunteeringInterest()).append(",")
                    .append(user.getLoanTaken()).append(",")
                    .append(user.getLoanOrganization()).append(",")
                    .append(user.getLinkedinUrl()).append(",")
                    .append(user.getDescription()).append(",")
                    .append(user.getContactShared()).append(",")
                    .append(user.getCountry()).append(",")
                    .append(user.getUserStudent()).append(",")
                    .append(user.getUserRole()).append(",")
                    .append(user.getGender()).append(",")
                    .append(user.getAboutMe()).append(",")
                    .append(user.getIsRegionalContact()).append("\n");

        }

        return csvContent.toString();
    }
}
