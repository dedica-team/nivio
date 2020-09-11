import { withBasePath } from './BasePath';

it('should return base path with trailing slash', () => {
  let url = withBasePath('');
  expect(url.endsWith('/')).toBeTruthy();

  url = withBasePath('/');
  expect(url.endsWith('/')).toBeTruthy();
});

it('should add a sub path', () => {
  let url = withBasePath('foobar');
  expect(url.endsWith('/foobar')).toBeTruthy();
});

it('should add a sub path without double slash', () => {
  let url = withBasePath('/foobar');
  expect(url.endsWith('/foobar')).toBeTruthy();
  expect(url.indexOf('//foobar')).toEqual(-1);
});
