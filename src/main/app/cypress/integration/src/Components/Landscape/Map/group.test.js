describe('Group test', () => {
  beforeEach(() => {
    cy.visit(Cypress.env('URL'));
  });
  it('Visits Nivio and checks the groups', () => {
    cy.wait(1000);
      cy.get('.ReactSVGPanZoom > svg:nth-child(1)').click();
      cy.get('[style="position: absolute; right: 0.5em;"] > .MuiButtonBase-root').click();

      cy.get('[class="groupArea unselected"]').each(($el, index, $list) => {
        cy.get('[class^="MuiPaper-root MuiCard-root "]').should('not.exist');
        cy.wrap($el).click({ force: true });
        console.log($el.find('[class="groupLabel"]').text());
        cy.get('[class^="MuiPaper-root MuiCard-root "]').should('be.visible');
        cy.get('.MuiCardHeader-root').should('contain.text', $el.find('[class="label"]').text(), {
          matchCase: false,
        });
        cy.get('.MuiCardHeader-action > .MuiButtonBase-root').click();
      });
  });
});
