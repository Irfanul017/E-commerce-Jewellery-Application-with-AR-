export { loadAsBlob, loadAsUrl, preload };
import { Config } from './schema';
declare function preload(config: Config): Promise<void>;
declare function loadAsUrl(url: string, config: Config): Promise<string>;
declare function loadAsBlob(key: string, config: Config): Promise<Blob>;
//# sourceMappingURL=resource.d.ts.map