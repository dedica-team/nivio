import axios from 'axios';
import { withBasePath } from './BasePath';

export default axios.create({
  withCredentials: true,
  baseURL: withBasePath(''),
});
