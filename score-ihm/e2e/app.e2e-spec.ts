import { ScoreIhm.NewPage } from './app.po';

describe('score-ihm.new App', () => {
  let page: ScoreIhm.NewPage;

  beforeEach(() => {
    page = new ScoreIhm.NewPage();
  });

  it('should display welcome message', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('Welcome to app!');
  });
});
