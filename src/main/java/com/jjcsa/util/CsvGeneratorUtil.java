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
                    String educationString = education.getUniversityName() + " - " + education.getDegree();
                    csvContent.append(getCsvString(educationString));
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
                    String workExString = workEx.getCompanyName() + " - " + workEx.getRole();
                    csvContent.append(getCsvString(workExString));
                }
                csvContent.append("\",");
            }

            csvContent.append(getCsvString(user.getLastUpdatedDate())).append(",")
                    .append(getCsvString(user.getApprovedDate())).append(",")
                    .append(getCsvString(user.getFirstName())).append(",")
                    .append(getCsvString(user.getMiddleName())).append(",")
                    .append(getCsvString(user.getLastName())).append(",")
                    .append(getCsvString(user.getMobileNumber())).append(",")
                    .append(getCsvString(user.getContactMethod())).append(",")
                    .append(getCsvString(user.getCommunityName())).append(",")
                    .append(getCsvString(user.getUserStatus())).append(",")
                    .append(getCsvString(user.getStreet())).append(",")
                    .append(getCsvString(user.getCity())).append(",")
                    .append(getCsvString(user.getState())).append(",")
                    .append(getCsvString(user.getZip())).append(",")
                    .append(getCsvString(user.getDateOfBirth())).append(",")
                    .append(getCsvString(user.getSocialMediaPlatform())).append(",")
                    .append(getCsvString(user.getVolunteeringInterest())).append(",")
                    .append(getCsvString(user.getLoanTaken())).append(",")
                    .append(getCsvString(user.getLoanOrganization())).append(",")
                    .append(getCsvString(user.getLinkedinUrl())).append(",")
                    .append(getCsvString(user.getDescription())).append(",")
                    .append(getCsvString(user.getContactShared())).append(",")
                    .append(getCsvString(user.getCountry())).append(",")
                    .append(getCsvString(user.getUserStudent())).append(",")
                    .append(getCsvString(user.getUserRole())).append(",")
                    .append(getCsvString(user.getGender())).append(",")
                    .append(getCsvString(user.getAboutMe())).append(",")
                    .append(getCsvString(user.getIsRegionalContact())).append("\n");

        }

        return csvContent.toString();
    }

    private String getCsvString(Object data) {
        String dataStr = String.valueOf(data); // converts null objects to "null" string
        if (dataStr.equals("null")) {
            return "";
        }

        // replace all line breaks with space
        dataStr = dataStr.replace("\n", " ");

        return dataStr;
    }
}
