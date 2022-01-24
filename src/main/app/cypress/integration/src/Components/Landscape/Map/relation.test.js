describe('Relation test', () => {
  beforeEach(() => {
    cy.visit('');
  });
  it('Visits Nivio and checks the relations', () => {
    cy.wait(1000);
    cy.get('.ReactSVGPanZoom > svg:nth-child(1)').click();
    cy.get('[style="position: absolute; right: 0.5em;"] > .MuiButtonBase-root').click();

    cy.get('[class="relation unselected"]').each(($el, index, $list) => {
      cy.get('[class^="MuiPaper-root MuiCard-root "]').should('not.exist');
      cy.wrap($el).click({ force: true });
      console.log($el.find('[class="label"]').text());
      cy.get('[class^="MuiPaper-root MuiCard-root "]').should('be.visible');
      cy.get('.MuiCardHeader-action > .MuiButtonBase-root').click();
    });
  });
});
