import 'zone.js'; // Included with Angular CLI.

// Polyfills for Node.js globals in browser environment
(window as any).global = window;
(window as any).Buffer = (window as any).Buffer || {};
(window as any).process = (window as any).process || { env: {}, nextTick: function(fn: Function) { setTimeout(fn, 0); } };
