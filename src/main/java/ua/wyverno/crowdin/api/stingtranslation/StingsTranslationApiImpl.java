package ua.wyverno.crowdin.api.stingtranslation;

import com.crowdin.client.core.http.HttpClient;
import com.crowdin.client.stringtranslations.StringTranslationsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;
import ua.wyverno.crowdin.api.stingtranslation.queries.StringsTranslationApprovalsListQuery;
import ua.wyverno.crowdin.api.stingtranslation.queries.StringsTranslationLanguageListQuery;
import ua.wyverno.crowdin.api.stingtranslation.queries.StringsTranslationListQuery;

import java.lang.reflect.Field;


@Component
public class StingsTranslationApiImpl implements StringTranslationAPI {
    private final StringTranslationsApi stringTranslationsApi;

    /**
     * {@link HttpClient} Клієнт Кроудіна, цей об'єкт витягується через Рефлексію.<br/>
     * Потрібен для того, щоб надіслати запит, якщо у бібліотеці Кроудіна не вистачає всіх параметрів які зазначені на оф. сторінці Crowdin API<br/>
     * Як приклад - <a href="https://support.crowdin.com/developer/api/v2/#tag/String-Translations/operation/api.projects.languages.translations.getMany">List Language Translation</a> - є такі параметри як "approvedOnly" та "orderBy", <br/>
     * Але в {@link StringTranslationsApi#listLanguageTranslations(Long projectId, String languageId, String stringIds,
     *                                                          String labelIds, Long fileId, Long branchId, Long directoryId,
     *                                                          String croql, Integer denormalizePlaceholders,
     *                                                          Integer limit, Integer offset)}<br/>
     * Цей метод не має такого поля як "approvedOnly" та "orderBy", щоб це виправити, запити будуть отримувати {@link HttpClient} та самостійно виконувати тіло метода, але окрім цього, вони ще будуть додавати ці параметри
     */
    private final HttpClient crowdinHttpClient;
    private final String crowdinBastApiURL;
    @Autowired
    public StingsTranslationApiImpl(CrowdinApiClient crowdinApiClient) throws NoSuchFieldException, IllegalAccessException {
        this.stringTranslationsApi = crowdinApiClient.getCrowdinClient().getStringTranslationsApi();

        // Дістаємо HttpClient Кроудіна та URL для запитів
        Class<?> supperClass = this.stringTranslationsApi
                .getClass()
                .getSuperclass();
        Field httpClientField = supperClass
                .getDeclaredField("httpClient");
        Field urlField = supperClass
                .getDeclaredField("url");
        httpClientField.setAccessible(true);
        urlField.setAccessible(true);
        this.crowdinHttpClient = (HttpClient) httpClientField.get(this.stringTranslationsApi);
        this.crowdinBastApiURL = (String) urlField.get(this.stringTranslationsApi);
    }

    @Override
    public StringsTranslationLanguageListQuery listLanguageTranslations(long projectID) {
        return new StringsTranslationLanguageListQuery(this.crowdinHttpClient, this.crowdinBastApiURL, projectID);
    }

    @Override
    public StringsTranslationApprovalsListQuery listTranslationApprovals(long projectID) {
        return new StringsTranslationApprovalsListQuery(this.crowdinHttpClient, this.crowdinBastApiURL, projectID);
    }

    @Override
    public StringsTranslationListQuery listTranslation(long projectID) {
        return new StringsTranslationListQuery(this.crowdinHttpClient, this.crowdinBastApiURL, projectID);
    }
}
