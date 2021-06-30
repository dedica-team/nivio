export interface SaveSearchConfig {
  /**
   * The search string as used in the GUI (lucene style)
   */
  searchTerm?: string;

  /**
   * The search title
   */
  title?: string;

  /**
   * The report to generate.
   */
  reportType: string | undefined;
}
