package pl.futurecollars.invoicing.controller.company

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.controller.AbstractControllerTest
import pl.futurecollars.invoicing.model.Company
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoicing.Helpers.TestHelpers.company

@Unroll
class CompanyControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc


    def "get all companies returns empty array when no companies added"() {
        expect:
        getAllCompanies() == []
    }

    def "all companies are returned"() {
        given:
        def numberOfCompanies = 3
        def expectedCompany = addUniqueCompanies(numberOfCompanies)

        when:
        def companies = getAllCompanies()

        then:
        companies.sort { it.id } == expectedCompany
    }

    def "add company returns sequential id"() {

        given:
        Company company = company(2)

        when:
        def id = addCompanyAndReturnId(company)

        then:
        addCompanyAndReturnId(company) == id + 1
        addCompanyAndReturnId(company) == id + 2
        addCompanyAndReturnId(company) == id + 3
    }

    def "get by id returns correct company"() {
        given:
        def expectedCompanies = addUniqueCompanies(5)
        def verifiedCompany = expectedCompanies.get(2)

        when:
        def company = getCompanyById(verifiedCompany.getId())

        then:
        company == verifiedCompany
    }

    def "404 is returned when getting not existing company"() {
        given:
        addUniqueCompanies(10)


        expect:
        mockMvc.perform(get("$COMPANY_ENDPOINT/$id"))
                .andExpect(status().isNotFound())

        where:
        id << [-2, 0, 500]
    }

    def "company can be modified"() {
        given:
        def id = addCompanyAndReturnId(company(4))
        def updatedCompany = company(1)
        updatedCompany.id = id

        expect:
        mockMvc.perform(
                put("$COMPANY_ENDPOINT/$id")
                        .content(jsonService.toJsonObject(updatedCompany))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent())

        def companyFromDbAfterUpdate = getCompanyById(id).toString()
        def expectedCompany = updatedCompany.toString()
        companyFromDbAfterUpdate == expectedCompany
    }


    def "can delete company"() {
        given:
        def companies = addUniqueCompanies(10)

        expect:
        companies.each { company -> deleteCompany(company.getId()) }
        getAllCompanies().size() == 0
    }


}
