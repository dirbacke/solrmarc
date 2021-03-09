package org.solrmarc.mixin.ssb;

import org.solrmarc.index.SolrIndexerMixin;
import org.solrmarc.mixin.helper.AuthorInformation;
import org.marc4j.marc.Record;
import java.util.Set;

public class SSBAuthorMixin extends SolrIndexerMixin {
	
	/**
	 * getCoAuthors reads the field 700 and return a list of co-authors
	 * @param record The entire marc record
	 * @return A list with the Co authors from Marc field 700
	 */
    public Set<String> getCoAuthors(final Record record) {
    	AuthorInformation authorInformation = new AuthorInformation(record);
        String fieldNumber = "700";
		char[] sections = new char[]{'a', 'b', 'c', 'd', 'e', 'g'};
        return authorInformation.getCoAuthors(fieldNumber, sections);
    }

	/**
	 * getIllustrators reads the field 700 and return a list of illustrators
	 * @param record The entire marc record
	 * @return A list with illustrators from Marc field 700
	 */
    public Set<String> getIllustrators(final Record record) {
    	AuthorInformation authorInformation = new AuthorInformation(record);
        String fieldNumber = "700";
		char[] sections = new char[]{'a', 'b', 'c', 'd', 'e', 'g'};
        return authorInformation.getIllustrators(fieldNumber, sections);
    }

	/**
	 * getTranslators reads the field 700 and return a list of translators
	 * @param record The entire marc record
	 * @return A list with translators from Marc field 700
	 */
	public Set<String> getTranslators(final Record record) {
		AuthorInformation authorInformation = new AuthorInformation(record);
		String fieldNumber = "700";
		char[] sections = new char[]{'a', 'b', 'c', 'd', 'e', 'g'};
		return authorInformation.getTranslators(fieldNumber, sections);
	}

	/**
	 * getOtherAuthors reads the field 700 and return a list of other authors
	 * @param record The entire marc record
	 * @return A list with other authors from Marc field 700
	 */
	public Set<String> getOtherAuthors(final Record record) {
		AuthorInformation authorInformation = new AuthorInformation(record);
		String fieldNumber = "700";
		char[] sections = new char[]{'a', 'b', 'c', 'd', 'e', 'g'};
		return authorInformation.getOtherAuthors(fieldNumber, sections);
	}
}
