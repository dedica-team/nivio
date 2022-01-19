describe('Item test', () => {
  beforeEach(() => {
    cy.visit('http://localhost:3000');
  });
  it('Visits Nivio and checks the items', () => {
    cy.wait(400);
    cy.get('[href^="#/landscape/"]').first().click();
    cy.url().should('include', '/landscape/');
    cy.wait(400);
    cy.get('.ReactSVGPanZoom > svg:nth-child(1)').click();
    cy.wait(400);
    cy.get('[class="item unselected"]').each(($el, index, $list) => {
      cy.get('[class^="makeStyles-sideBar-"]').should('not.be.visible');
      cy.wrap($el).click();
      console.log($el.find('[class="label"]').text());
      cy.get('[class^="makeStyles-sideBar-"]').should('be.visible');
      cy.get('.MuiCardHeader-root').contains($el.find('[class="label"]').text(), {
        matchCase: false,
      });
      cy.get('.MuiCardHeader-action > .MuiButtonBase-root').click();
    });
  });
});
