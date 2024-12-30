export { createOnnxSession, runOnnxSession };
import ndarray, { NdArray } from 'ndarray';
import type ORT from 'onnxruntime-web';
import { Config } from './schema';
declare function createOnnxSession(model: any, config: Config): Promise<ORT.InferenceSession>;
declare function runOnnxSession(session: any, inputs: [string, NdArray<Float32Array>][], outputs: [string], config: Config): Promise<ndarray.NdArray<Float32Array>[]>;
//# sourceMappingURL=onnx.d.ts.map